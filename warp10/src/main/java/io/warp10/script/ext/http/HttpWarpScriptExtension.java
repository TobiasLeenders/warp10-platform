//
//   Copyright 2021  SenX S.A.S.
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

package io.warp10.script.ext.http;

import io.warp10.warp.sdk.WarpScriptExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * Extension for HTTP and the associated function to change limits: MAXURLCOUNT and MAXDOWNLOADSIZE
 */
public class HttpWarpScriptExtension extends WarpScriptExtension {

  //
  // AUTHORIZATION
  //

  /**
   * If set to true, HTTP requires the stack to be authenticated
   */
  public static final String HTTP_AUTHENTICATION_REQUIRED = "warpscript.http.authentication.required";

  /**
   * If set, this capability is inspected
   */
  public static final String HTTP_CAPABILITY = "warpscript.http.capability";

  //
  // CONFIGURATION
  //

  /**
   * Allowed and excluded host patterns.
   */
  public static final String WARPSCRIPT_HTTP_HOST_PATTERNS = "warpscript.http.host.patterns";

  //
  // STACK
  //

  /**
   * Number of calls to HTTP so far in the sessions
   */
  public static final String ATTRIBUTE_HTTP_COUNT = "http.requests";

  /**
   * Current  HTTP so far in the sessions
   */
  public static final String ATTRIBUTE_HTTP_SIZE = "http.size";

  //
  // DEFAULTS
  //

  public static final long DEFAULT_HTTP_LIMIT = 1L;
  public static final long DEFAULT_HTTP_MAXSIZE = 65536L;

  //
  // Init extension
  //

  private static final Map<String, Object> functions = new HashMap<String, Object>();
  static {
    functions.put("HTTP", new HTTP("HTTP"));
  }

  public Map<String, Object> getFunctions() {
    return functions;
  }
}
