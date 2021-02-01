package com.xjt.mutilevel.util;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 类说明
 *
 * @author sunney
 * @version V1.0  创建时间：2019年6月4日 上午11:34:21
 */
public class ExplUtils {


    public static String generateKey(String key, StandardEvaluationContext context) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(key);
        String value = exp.getValue(context, String.class);
        return value;
    }

}
 