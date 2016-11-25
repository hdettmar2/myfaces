/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package javax.faces.component.search;

import java.util.List;
import javax.faces.FacesWrapper;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;

/**
 *
 */
public abstract class SearchExpressionHandlerWrapper extends SearchExpressionHandler
        implements FacesWrapper<SearchExpressionHandler>
{

    @Override
    public abstract SearchExpressionHandler getWrapped();

    @Override
    public List<String> resolveClientIds(SearchExpressionContext searchExpressionContext, String expressions)
    {
        return getWrapped().resolveClientIds(searchExpressionContext, expressions);
    }

    @Override
    public void resolveComponent(SearchExpressionContext searchExpressionContext, 
            String expression, ContextCallback callback)
    {
        getWrapped().resolveComponent(searchExpressionContext, expression, callback);
    }

    @Override
    public void invokeOnComponentFromExpression(SearchExpressionContext searchExpressionContext, 
            UIComponent last, String expression, ContextCallback topCallback)
    {
        getWrapped().invokeOnComponentFromExpression(searchExpressionContext, last, expression, topCallback);
    }

    @Override
    public void invokeOnComponentFromExpressions(SearchExpressionContext searchExpressionContext, 
            UIComponent last, String[] expressions, ContextCallback topCallback)
    {
        getWrapped().invokeOnComponentFromExpressions(searchExpressionContext, last, expressions, topCallback);
    }

    @Override
    public void applyKeyword(SearchExpressionContext searchExpressionContext, 
            UIComponent last, String command, ContextCallback topCallback)
    {
        getWrapped().applyKeyword(searchExpressionContext, last, command, topCallback);
    }

    @Override
    public boolean isValidExpression(SearchExpressionContext searchExpressionContext, String expression)
    {
        return getWrapped().isValidExpression(searchExpressionContext, expression);
    }

    @Override
    public boolean isPassthroughExpression(SearchExpressionContext searchExpressionContext, String expression)
    {
        return getWrapped().isPassthroughExpression(searchExpressionContext, expression);
    }

    @Override
    public String[] splitExpressions(String expressions)
    {
        return getWrapped().splitExpressions(expressions);
    }

    @Override
    public void resolveComponents(
            SearchExpressionContext searchExpressionContext, String expressions, ContextCallback callback)
    {
        getWrapped().resolveComponents(searchExpressionContext, expressions, callback);
    }

    @Override
    public String resolveClientId(SearchExpressionContext searchExpressionContext, String expressions)
    {
        return getWrapped().resolveClientId(searchExpressionContext, expressions);
    }

}
