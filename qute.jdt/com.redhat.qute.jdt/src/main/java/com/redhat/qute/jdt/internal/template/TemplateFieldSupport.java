/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.redhat.qute.jdt.internal.template;

import static com.redhat.qute.jdt.internal.QuteJavaConstants.LOCATION_ANNOTATION;
import static com.redhat.qute.jdt.utils.JDTQuteProjectUtils.getTemplatePath;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;

import com.redhat.qute.commons.datamodel.DataModelParameter;
import com.redhat.qute.commons.datamodel.DataModelTemplate;
import com.redhat.qute.jdt.utils.AnnotationUtils;

/**
 * Template field support.
 * 
 * <code>
 *  &#64;Inject
 *  Template hello;
 *
 *  ...
 *
 *   &#64;GET
 *   &#64;Produces(MediaType.TEXT_HTML)
 *   public TemplateInstance get(@QueryParam("name") String name) {
 *   	hello.data("age", 12);
 *   	hello.data("height", 1.50, "weight", 50L);
 *       return hello.data("name", name);
 *   }
 *   </code>
 * 
 * @author Angelo ZERR
 *
 */
class TemplateFieldSupport {

	private static final Logger LOGGER = Logger.getLogger(TemplateFieldSupport.class.getName());

	public static void collectDataModelTemplateForTemplateField(IField field,
			List<DataModelTemplate<DataModelParameter>> templates, IProgressMonitor monitor) {
		DataModelTemplate<DataModelParameter> template = createTemplateDataModel(field, monitor);
		templates.add(template);
	}

	private static DataModelTemplate<DataModelParameter> createTemplateDataModel(IField field,
			IProgressMonitor monitor) {

		String location = getLocation(field);
		String fieldName = field.getElementName();
		// src/main/resources/templates/${methodName}.qute.html
		String templateUri = getTemplatePath(null, location != null ? location : fieldName);

		// Create template data model with:
		// - template uri : Qute template file which must be bind with data model.
		// - source type : the Java class which defines Templates
		// -
		DataModelTemplate<DataModelParameter> template = new DataModelTemplate<DataModelParameter>();
		template.setParameters(new ArrayList<>());
		template.setTemplateUri(templateUri);
		template.setSourceType(field.getDeclaringType().getFullyQualifiedName());
		template.setSourceField(fieldName);
		// Collect data parameters for the given template
		TemplateDataSupport.collectParametersFromDataMethodInvocation(field, template, monitor);
		return template;
	}

	private static String getLocation(IField field) {
		try {
			IAnnotation annotation = AnnotationUtils.getAnnotation(field, LOCATION_ANNOTATION);
			if (annotation != null) {
				return AnnotationUtils.getAnnotationMemberValue(annotation, "value");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while getting @Location of '" + field.getElementName() + "'.", e);
		}
		return null;
	}
}
