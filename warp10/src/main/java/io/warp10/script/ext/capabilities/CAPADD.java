//
//   Copyright 2020  SenX S.A.S.
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

package io.warp10.script.ext.capabilities;

import java.util.Map.Entry;

import io.warp10.continuum.Tokens;
import io.warp10.quasar.token.thrift.data.ReadToken;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;
import io.warp10.script.WarpScriptStackFunction;

public class CAPADD extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  public CAPADD(String name) {
    super(name);
  }
  
  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
    
    Object top = stack.pop();
    
    if (!(top instanceof String)) {
      throw new WarpScriptException(getName() + " expects a TOKEN.");
    }
    
    String token = (String) top;
    
    ReadToken rtoken = Tokens.extractReadToken(token);

    if (null == rtoken) {
      throw new WarpScriptException(getName() + " invalid READ TOKEN.");
    }
    
    if (rtoken.getAttributesSize() > 0) {
      Capabilities capabilities = null;
      
      if (stack.getAttribute(CapabilitiesWarpScriptExtension.CAPABILITIES_ATTR) instanceof Capabilities) {
        capabilities = (Capabilities) stack.getAttribute(CapabilitiesWarpScriptExtension.CAPABILITIES_ATTR);
      }
      
      for (Entry<String,String> entry: rtoken.getAttributes().entrySet()) {
        if (entry.getKey().startsWith(CapabilitiesWarpScriptExtension.CAPABILITIES_PREFIX)) {
          if (null == capabilities) {
            capabilities = new Capabilities();
            stack.setAttribute(CapabilitiesWarpScriptExtension.CAPABILITIES_ATTR, capabilities);
          }
          capabilities.capabilities.putIfAbsent(entry.getKey().substring(CapabilitiesWarpScriptExtension.CAPABILITIES_PREFIX.length()), entry.getValue());
        }
      }
    }
    
    return stack;
  }  
}
