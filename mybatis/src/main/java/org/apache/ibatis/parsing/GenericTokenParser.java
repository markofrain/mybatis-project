/**
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.parsing;

/**
 * @author Clinton Begin
 */
public class GenericTokenParser {

  // 占位符开始标记
  private final String openToken;
  // 占位符结束标记
  private final String closeToken;
  // 实现类会按照一定逻辑解析占位符
  private final TokenHandler handler;

  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  public String parse(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }
    // 查找开始标记
    int start = text.indexOf(openToken);
    if (start == -1) {
      return text;
    }
    char[] src = text.toCharArray();
    int offset = 0;
    // 用来记录解析后的字符串。解析并替换${}后的全部字符串
    final StringBuilder builder = new StringBuilder();
    // 用来记录一个占位符的字面值${}里面的值
    StringBuilder expression = null;
    do {
      if (start > 0 && src[start - 1] == '\\') {
        // this open token is escaped. remove the backslash and continue.
        // 遇到转义开始标记，直接将前面的字符串以及开始标记追加到builder中
        builder.append(src, offset, start - offset - 1).append(openToken);
        offset = start + openToken.length();
      } else {
        // found open token. let's search close token.
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        // 将开始标记前的字符追加到builder中
        builder.append(src, offset, start - offset);
        // 修改offset位置
        offset = start + openToken.length();
        // 向后查找结束标记
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {
          // 处理结束标记的转义
          if (end > offset && src[end - 1] == '\\') {
            // this close token is escaped. remove the backslash and continue.
            // 追加
            expression.append(src, offset, end - offset - 1).append(closeToken);
            // 设置offset位置
            offset = end + closeToken.length();
            // 重新向后获取结束标记
            end = text.indexOf(closeToken, offset);
          } else {
            // 将开始标记和结束标记之间的字符串追加到 expression 中保存
            expression.append(src, offset, end - offset);
            break;
          }
        }
        if (end == -1) {
          // 未找到结束标记,追加从开始到最后
          // close token was not found.
          builder.append(src, start, src.length - start);
          offset = src.length;
        } else {
          // 找到了，解析并追加到builder
          builder.append(handler.handleToken(expression.toString()));
          offset = end + closeToken.length();
        }
      }
      // 解析完一个，重新设置start
      start = text.indexOf(openToken, offset);
    } while (start > -1);
    if (offset < src.length) {
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}
