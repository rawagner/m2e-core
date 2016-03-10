/*******************************************************************************
 * Copyright (c) 2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.core.internal.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.ResolverConfiguration;


/**
 * @TODO anyone can think of a better name?
 */
public class ResolverConfigurationIO {
  private static final Logger log = LoggerFactory.getLogger(ResolverConfigurationIO.class);

  /**
   * Configuration version project preference key.
   */
  private static final String P_VERSION = "version"; //$NON-NLS-1$

  /**
   * Workspace dependency resolution project preference key. Boolean, <code>true</code> means workspace dependency
   * resolution is enabled.
   */
  private static final String P_RESOLVE_WORKSPACE_PROJECTS = "resolveWorkspaceProjects"; //$NON-NLS-1$

  /**
   * Active profiles project preference key. Value is comma-separated list of enabled profiles.
   */
  //FIXME Bug 337353 Can't rename the preference key as it would break existing projects 
  private static final String P_SELECTED_PROFILES = "activeProfiles"; //$NON-NLS-1$

  /**
   * Lifecycle mapping id configured for the project explicitly.
   */
  private static final String P_LIFECYCLE_MAPPING_ID = "lifecycleMappingId";

  private static final String P_PROPERTIES = "properties";

  /**
   * Current configuration version value. See {@link #P_VERSION}
   */
  private static final String VERSION = "1"; //$NON-NLS-1$

  public static boolean saveResolverConfiguration(IProject project, ResolverConfiguration configuration) {
    IScopeContext projectScope = new ProjectScope(project);
    IEclipsePreferences projectNode = projectScope.getNode(IMavenConstants.PLUGIN_ID);
    if(projectNode != null) {
      projectNode.put(P_VERSION, VERSION);

      projectNode.putBoolean(P_RESOLVE_WORKSPACE_PROJECTS, configuration.shouldResolveWorkspaceProjects());

      projectNode.put(P_SELECTED_PROFILES, configuration.getSelectedProfiles());

      if(configuration.getLifecycleMappingId() != null) {
        projectNode.put(P_LIFECYCLE_MAPPING_ID, configuration.getLifecycleMappingId());
      } else {
        projectNode.remove(P_LIFECYCLE_MAPPING_ID);
      }

      if(configuration.getProperties() != null && !configuration.getProperties().isEmpty()) {
        projectNode.put(P_PROPERTIES, propertiesAsString(configuration.getProperties()));
      } else {
        projectNode.remove(P_PROPERTIES);
      }

      try {
        projectNode.flush();
        return true;
      } catch(BackingStoreException ex) {
        log.error("Failed to save resolver configuration", ex);
      }
    }

    return false;
  }

  public static ResolverConfiguration readResolverConfiguration(IProject project) {
    IScopeContext projectScope = new ProjectScope(project);
    IEclipsePreferences projectNode = projectScope.getNode(IMavenConstants.PLUGIN_ID);
    if(projectNode == null) {
      return new ResolverConfiguration();
    }

    String version = projectNode.get(P_VERSION, null);
    if(version == null) { // migrate from old config
      // return LegacyBuildPathManager.getResolverConfiguration(project);
      return new ResolverConfiguration();
    }

    ResolverConfiguration configuration = new ResolverConfiguration();
    configuration.setResolveWorkspaceProjects(projectNode.getBoolean(P_RESOLVE_WORKSPACE_PROJECTS, false));
    configuration.setSelectedProfiles(projectNode.get(P_SELECTED_PROFILES, "")); //$NON-NLS-1$
    configuration.setLifecycleMappingId(projectNode.get(P_LIFECYCLE_MAPPING_ID, (String) null));
    configuration.setProperties(stringAsProperties(projectNode.get(P_PROPERTIES, null)));
    return configuration;
  }

  private static String propertiesAsString(Properties properties) {
    StringBuilder props = new StringBuilder();
    boolean separate = false;
    for(Object obj : properties.keySet()) {
      String value = properties.get(obj).toString();
      String key = obj.toString();

      value = value.replaceAll(";", "$0$0");

      if(separate) {
        props.append(";");
      }
      props.append(key + ">" + value);
      separate = true;
    }
    return props.toString();
  }

  private static Properties stringAsProperties(String stringProperties) {
    if(stringProperties == null) {
      return null;
    }
    Properties properties = new Properties();

    List<String> propertiesList = parseWithSeparator(';', stringProperties.toCharArray());

    for(String p : propertiesList) {
      String[] props1 = p.split(">", 2);
      properties.put(props1[0], props1[1].replaceAll(";;", ";"));
    }
    return properties;
  }

  private static List<String> parseWithSeparator(char separator, char[] chars) {
    List<String> properties = new ArrayList<String>();
    int lastPropIndex = 0;
    for(int i = 0; i < chars.length; i++ ) {
      if(chars[i] == separator) {

        if(!(i + 1 < chars.length && chars[i + 1] == separator)) {
          boolean isSeparator = false;
          for(int y = i - 1; y >= 0; y-- ) {
            if(chars[y] != separator) {
              int indexDiff = i - y - 1;
              isSeparator = indexDiff % 2 == 0;
              break;
            }
          }
          if(isSeparator) {
            properties.add(new String(chars, lastPropIndex, i - lastPropIndex));
            lastPropIndex = ++i;
          }
        }
      }
    }
    properties.add(new String(chars, lastPropIndex, chars.length - lastPropIndex));
    return properties;
  }

}
