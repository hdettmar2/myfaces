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

package org.apache.myfaces.component.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.application.StateManager;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.search.ComponentNotFoundException;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchExpressionHandler;
import javax.faces.component.search.SearchExpressionHint;
import javax.faces.context.FacesContext;
import org.apache.myfaces.mc.test.core.AbstractMyFacesRequestTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class SearchExpressionImplTest extends AbstractMyFacesRequestTestCase
{
    @Override
    protected boolean isScanAnnotations()
    {
        return true;
    }

    @Override
    protected void setUpWebConfigParams() throws Exception
    {
        super.setUpWebConfigParams();
        servletContext.addInitParameter("org.apache.myfaces.annotation.SCAN_PACKAGES","org.apache.myfaces.component.search");
        servletContext.addInitParameter(StateManager.STATE_SAVING_METHOD_PARAM_NAME, StateManager.STATE_SAVING_METHOD_CLIENT);
        servletContext.addInitParameter("javax.faces.PARTIAL_STATE_SAVING", "true");
    }

    @Test
    public void testSearchExpression1() throws Exception
    {
        startViewRequest("/search1.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();

        UIOutput out = (UIOutput) facesContext.getViewRoot().findComponent("mainForm:showName");
        SearchExpressionContext searchContext = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out);
        Collection<String> list = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "mainForm:showName");

        boolean found = false;
        for (String clientId : list)
        {
            System.out.println(clientId);
            found = true;
        }
        Assert.assertTrue(found);
        
        String componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@this").get(0);
        Assert.assertEquals(componentId, "mainForm:showName");
        
        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@this:@parent:showName").get(0);
        Assert.assertEquals(componentId, "mainForm:showName");

        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@parent:showName:@parent:showName").get(0);
        Assert.assertEquals(componentId, "mainForm:showName");
        
        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@form:showName").get(0);
        Assert.assertEquals(componentId, "mainForm:showName");

        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@namingcontainer:showName").get(0);
        Assert.assertEquals(componentId, "mainForm:showName");

        UIOutput name = (UIOutput) facesContext.getViewRoot().findComponent("mainForm:name");
        searchContext = SearchExpressionContext.createSearchExpressionContext(facesContext, name);
        
        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@previous").get(0);
        Assert.assertEquals(componentId, "mainForm:labelName");
        
        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@next").get(0);
        Assert.assertEquals(componentId, "mainForm:msgName");
        
        componentId = facesContext.getApplication().getSearchExpressionHandler().resolveClientIds(
                searchContext, "@parent:@id(msgName)").get(0);
        Assert.assertEquals(componentId, "mainForm:msgName");
        
        facesContext.getViewRoot().invokeOnComponent(facesContext, "mainForm:table:3:nested:1:nestedButton", 
                new ContextCallback()
        {
            @Override
            public void invokeContextCallback(FacesContext context, UIComponent target)
            {
                SearchExpressionHandler handler = facesContext.getApplication().getSearchExpressionHandler();
                SearchExpressionContext searchContext = 
                        SearchExpressionContext.createSearchExpressionContext(context, target);
                String componentId = handler.resolveClientIds(searchContext, "mainForm:showName").get(0);
                Assert.assertEquals(componentId, "mainForm:showName");
                
                componentId = handler.resolveClientIds(searchContext, "nested:nestedText").get(0);
                Assert.assertEquals(componentId, "mainForm:table:3:nested:1:nestedText");
                
                componentId = handler.resolveClientIds(searchContext, "table:baseText").get(0);
                Assert.assertEquals(componentId, "mainForm:table:3:baseText");
                
                componentId = handler.resolveClientIds(searchContext, "table:0:baseText").get(0);
                Assert.assertEquals(componentId, "mainForm:table:0:baseText");

                componentId = handler.resolveClientIds(searchContext, "nested:0:nestedText").get(0);
                Assert.assertEquals(componentId, "mainForm:table:3:nested:0:nestedText");
                
                componentId = handler.resolveClientIds(searchContext, "table:nested").get(0);
                Assert.assertEquals(componentId, "mainForm:table:3:nested");
                
                componentId = handler.resolveClientIds(searchContext, "table:1:nested:0:nestedText").get(0);
                Assert.assertEquals(componentId, "mainForm:table:1:nested:0:nestedText");
                
                //System.out.println(componentId);
            }
        });
       
        processRemainingPhases();
    }
    
    @Test
    public void testResolveClientId1() throws Exception
    {
        startViewRequest("/search1.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();
        
        UIOutput out = (UIOutput) facesContext.getViewRoot().findComponent("mainForm:showName");
        Set<SearchExpressionHint> expressionHints = new HashSet<SearchExpressionHint>();
        expressionHints.add(SearchExpressionHint.PARENT_FALLBACK);
        SearchExpressionContext searchContextWithParentFallback = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out, expressionHints, null);
        SearchExpressionHandler handler = facesContext.getApplication().getSearchExpressionHandler();
        
        String clientId = handler.resolveClientId(searchContextWithParentFallback, "@none");
        Assert.assertNotNull(clientId);
        
        expressionHints = new HashSet<SearchExpressionHint>();
        expressionHints.add(SearchExpressionHint.IGNORE_NO_RESULT);
        SearchExpressionContext searchContextWithIgnoreNoResult = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out, expressionHints, null);
        
        clientId = handler.resolveClientId(searchContextWithIgnoreNoResult, "@none");
        Assert.assertNull(clientId);
        
        SearchExpressionContext searchContext = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out);

        try 
        {
            clientId = handler.resolveClientId(searchContext, "@none");
            Assert.fail();
        }
        catch (ComponentNotFoundException e)
        {
            //No op
        }
    }
    
    @Test
    public void testResolveClientIds1() throws Exception
    {
        startViewRequest("/search1.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();
        
        UIOutput out = (UIOutput) facesContext.getViewRoot().findComponent("mainForm:showName");
        Set<SearchExpressionHint> expressionHints = new HashSet<SearchExpressionHint>();
        expressionHints.add(SearchExpressionHint.PARENT_FALLBACK);
        SearchExpressionContext searchContextWithParentFallback = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out, expressionHints, null);
        SearchExpressionHandler handler = facesContext.getApplication().getSearchExpressionHandler();
        
        List<String> clientId = handler.resolveClientIds(searchContextWithParentFallback, "@none");
        Assert.assertNotNull(clientId.get(0));
        
        expressionHints = new HashSet<SearchExpressionHint>();
        expressionHints.add(SearchExpressionHint.IGNORE_NO_RESULT);
        SearchExpressionContext searchContextWithIgnoreNoResult = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out, expressionHints, null);
        
        clientId = handler.resolveClientIds(searchContextWithIgnoreNoResult, "@none");
        Assert.assertTrue(clientId.isEmpty());
        
        SearchExpressionContext searchContext = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, out);

        try 
        {
            clientId = handler.resolveClientIds(searchContext, "@none");
            Assert.fail();
        }
        catch (ComponentNotFoundException e)
        {
            //No op
        }
    }

    @Test
    public void testIsValid() throws Exception
    {
        startViewRequest("/search1.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();

        SearchExpressionContext searchContext = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, null);
        SearchExpressionHandler handler = facesContext.getApplication().getSearchExpressionHandler();

        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:showName"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:3:nested:1:nestedText"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:3:baseText"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:0:baseText"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:3:nested:0:nestedText"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:3:nested"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "mainForm:table:1:nested:0:nestedText"));
        
        Assert.assertTrue(handler.isValidExpression(searchContext, "@this"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@this:@parent:showName"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@parent:showName:@parent:showName"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@form:showName"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@namingcontainer:showName"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@previous"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@next"));
        Assert.assertTrue(handler.isValidExpression(searchContext, "@parent:@id(msgName)"));
        
        Assert.assertFalse(handler.isValidExpression(searchContext, "@whoNows"));
        Assert.assertFalse(handler.isValidExpression(searchContext, "@parent:@whoNows"));
        Assert.assertFalse(handler.isValidExpression(searchContext, "mainForm:@whoNows"));
        Assert.assertFalse(handler.isValidExpression(searchContext, "!whoNows"));
        
        Assert.assertFalse(handler.isValidExpression(searchContext, "@none:@parent"));
        Assert.assertFalse(handler.isValidExpression(searchContext, "@all:@parent"));
    }
    
    @Test
    public void testIsPassthroughExpression() throws Exception
    {
        startViewRequest("/search1.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();

        SearchExpressionContext searchContext = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, null);
        SearchExpressionHandler handler = facesContext.getApplication().getSearchExpressionHandler();

        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:3:nested:1:nestedText"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:3:baseText"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:0:baseText"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:3:nested:0:nestedText"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:3:nested"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:table:1:nested:0:nestedText"));
        
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@this"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@this:@parent:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@parent:showName:@parent:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@form"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@form:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@namingcontainer:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@previous"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@next"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@parent:@id(msgName)"));
        
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@whoNows"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "@parent:@whoNows"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "mainForm:@whoNows"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContext, "!whoNows"));
        
        Set<SearchExpressionHint> expressionHints = new HashSet<SearchExpressionHint>();
        expressionHints.add(SearchExpressionHint.RESOLVE_AJAX);
        SearchExpressionContext searchContextWithAjaxResolve = 
                SearchExpressionContext.createSearchExpressionContext(facesContext, null, expressionHints, null);
        
        Assert.assertTrue(handler.isPassthroughExpression(searchContextWithAjaxResolve, "@form"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContextWithAjaxResolve, "@form:showName"));
        Assert.assertFalse(handler.isPassthroughExpression(searchContextWithAjaxResolve, "@form:@child(0)"));
    }

    @Test
    public void testCompositeComponentExpression() throws Exception
    {
        startViewRequest("/testCompositeActionSource.xhtml");
        processLifecycleExecute();
        executeBeforeRender();
        executeBuildViewCycle();
        
        
    }
    
}
