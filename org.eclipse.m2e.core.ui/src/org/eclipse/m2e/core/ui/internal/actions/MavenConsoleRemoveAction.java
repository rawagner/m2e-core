/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.core.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.m2e.core.internal.Messages;
import org.eclipse.m2e.core.ui.internal.M2EUIPluginActivator;
import org.eclipse.m2e.core.ui.internal.MavenImages;


public class MavenConsoleRemoveAction extends Action {

  public MavenConsoleRemoveAction() {
    setToolTipText(Messages.MavenConsoleRemoveAction_tooltip);
    setImageDescriptor(MavenImages.CLOSE);
  }

  public void run() {
    M2EUIPluginActivator.getDefault().getMavenConsoleImpl().closeConsole();
  }

}