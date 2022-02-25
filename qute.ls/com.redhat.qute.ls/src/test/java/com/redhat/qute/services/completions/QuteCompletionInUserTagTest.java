/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.services.completions;

import static com.redhat.qute.QuteAssert.SECTION_SNIPPET_SIZE;
import static com.redhat.qute.QuteAssert.c;
import static com.redhat.qute.QuteAssert.r;
import static com.redhat.qute.QuteAssert.testCompletionFor;

import org.junit.jupiter.api.Test;

/**
 * Tests for Qute completion with user tag section.
 * 
 * @author Angelo ZERR
 *
 */
public class QuteCompletionInUserTagTest {

	@Test
	public void userTag() throws Exception {
		String template = "{|";

		// Without snippet
		testCompletionFor(template, //
				false, // no snippet support
				SECTION_SNIPPET_SIZE, //
				c("formElement", "{#formElement /}", //
						r(0, 0, 0, 1)));

		// With snippet support
		testCompletionFor(template, //
				true, // snippet support
				SECTION_SNIPPET_SIZE, //
				c("formElement", "{#formElement /}$0", //
						r(0, 0, 0, 1)));
	}

	@Test
	public void userTagAndCloseBracket() throws Exception {
		String template = "{#f|or   }";

		// Without snippet
		testCompletionFor(template, //
				false, // no snippet support
				SECTION_SNIPPET_SIZE, //
				c("formElement", "{#formElement /}", //
						r(0, 0, 0, 3)));

		// With snippet support
		testCompletionFor(template, //
				true, // snippet support
				SECTION_SNIPPET_SIZE, //
				c("formElement", "{#formElement /}$0", //
						r(0, 0, 0, 3)));
	}

	@Test
	public void parameterExpressionInUserTag() throws Exception {
		String template = "{@org.acme.Item item}\r\n" + //
				"{#user name=| /}";
		testCompletionFor(template, //
				c("item", "item", r(1, 12, 1, 12)));

		template = "{@org.acme.Item item}\r\n" + //
				"{#user name=item.| /}";
		testCompletionFor(template, //
				c("base : String", "base", r(1, 17, 1, 17)), // comes from BaseItem extended by Item
				c("name : String", "name", r(1, 17, 1, 17)), //
				c("price : BigInteger", "price", r(1, 17, 1, 17)), //
				c("review : Review", "review", r(1, 17, 1, 17)), //
				c("review2 : Review", "review2", r(1, 17, 1, 17)), //
				c("getReview2() : Review", "getReview2", r(1, 17, 1, 17)));
	}

	@Test
	public void specialKeys() throws Exception {
		String template = "{@org.acme.Item item}\r\n" + //
				"{|}";

		// In qute template
		testCompletionFor(template, //
				SECTION_SNIPPET_SIZE + 1, //
				c("item", "item", r(1, 1, 1, 1)));

		// In user tag
		testCompletionFor(template, //				
				"src/main/resources/templates/tags/form.html", //
				"tags/form", //
				SECTION_SNIPPET_SIZE + 1 /* item */ + 2 /* it, nested-content */, //
				c("item", "item", r(1, 1, 1, 1)), //
				c("it", "it", r(1, 1, 1, 1)), //
				c("nested-content", "nested-content", r(1, 1, 1, 1)));

	}
}