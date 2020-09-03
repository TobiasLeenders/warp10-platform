//
//   Copyright 2018-2020  SenX S.A.S.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package io.warp10.script.functions;

import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStack.Macro;
import io.warp10.script.WarpScriptStackFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sort a list or a LinkedHashMap according to a macro
 */
public class SORTBY extends NamedWarpScriptFunction implements WarpScriptStackFunction {

  public SORTBY(String name) {
    super(name);
  }

  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
    Object top = stack.pop();

    if (!(top instanceof Macro)) {
      throw new WarpScriptException(getName() + " expects a macro on top of the stack.");
    }

    Macro macro = (Macro) top;

    top = stack.pop();

    if (top instanceof List) {
      List list = (List) top;

      Comparator comparator = buildComparator(stack, list, macro);

      List<Integer> indices = new ArrayList<Integer>(list.size());
      for (int i = 0; i < list.size(); i++) {
        indices.add(i);
      }

      // Sort the list of indices
      Collections.sort(indices, comparator);

      Object[] target = new Object[indices.size()];
      for (int i = 0; i < target.length; i++) {
        target[i] = list.get(indices.get(i));
      }

      list.clear();

      for (Object elt: target) {
        list.add(elt);
      }

      stack.push(list);
    } else if (top instanceof LinkedHashMap) {
      LinkedHashMap linkedHashMap = ((LinkedHashMap) top);
      ArrayList<Map.Entry> entryList = new ArrayList<Map.Entry>(linkedHashMap.entrySet());

      Comparator comparator = buildComparator(stack, entryList, macro);

      List<Integer> indices = new ArrayList<Integer>(linkedHashMap.size());
      for (int i = 0; i < linkedHashMap.size(); i++) {
        indices.add(i);
      }

      // Sort the list of indices
      Collections.sort(indices, comparator);

      Map.Entry[] target = new Map.Entry[indices.size()];
      for (int i = 0; i < target.length; i++) {
        target[i] = entryList.get(indices.get(i));
      }

      linkedHashMap.clear();


      for (Map.Entry entry: target) {
        linkedHashMap.put(entry.getKey(), entry.getValue());
      }

      stack.push(linkedHashMap);
    } else {
      throw new WarpScriptException(getName() + " operates on a list or a map created by {} or ->MAP.");
    }

    return stack;
  }

  private Comparator buildComparator(WarpScriptStack stack, Collection collection, Macro macro) throws WarpScriptException {
    //
    // Generate the result of the macro for the various elements
    //

    String type = null;

    Object values = null;

    int idx = 0;

    for (Object elt: collection) {
      if (elt instanceof Map.Entry) {
        stack.push(((Map.Entry) elt).getKey());
        stack.push(((Map.Entry) elt).getValue());
      } else {
        stack.push(elt);
      }
      stack.exec(macro);
      Object value = stack.pop();

      String valtype = null;

      if (value instanceof Long) {
        valtype = "LONG";
      } else if (value instanceof Double) {
        valtype = "DOUBLE";
      } else if (value instanceof String) {
        valtype = "STRING";
      }

      if (null == value || null == valtype || (null != type && (!type.equals(valtype)))) {
        throw new WarpScriptException(getName() + " expects its macro to return a non null double, long or string in a consistent manner.");
      }

      if (null == type) {
        switch (valtype) {
          case "LONG":
            values = new long[collection.size()];
            break;
          case "DOUBLE":
            values = new double[collection.size()];
            break;
          case "STRING":
            values = new String[collection.size()];
            break;
        }
        type = valtype;
      }

      switch (type) {
        case "LONG":
          ((long[]) values)[idx] = ((Number) value).longValue();
          break;
        case "DOUBLE":
          ((double[]) values)[idx] = ((Number) value).doubleValue();
          break;
        case "STRING":
          ((String[]) values)[idx] = value.toString();
          break;
      }

      idx++;
    }

    final String valtype = type;

    Comparator<Integer> comparator = null;

    if ("LONG".equals(valtype)) {
      final long[] lvalues = (long[]) values;

      comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i1, Integer i2) {
          if (lvalues[i1] < lvalues[i2]) {
            return -1;
          } else if (lvalues[i1] > lvalues[i2]) {
            return 1;
          } else {
            return 0;
          }
        }
      };
    } else if ("DOUBLE".equals(valtype)) {
      final double[] dvalues = (double[]) values;

      comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i1, Integer i2) {
          if (dvalues[i1] < dvalues[i2]) {
            return -1;
          } else if (dvalues[i1] > dvalues[i2]) {
            return 1;
          } else {
            return 0;
          }
        }
      };
    } else if ("STRING".equals(valtype)) {
      final String[] svalues = (String[]) values;

      comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i1, Integer i2) {
          return svalues[i1].compareTo(svalues[i2]);
        }
      };
    }

    return comparator;
  }
}
