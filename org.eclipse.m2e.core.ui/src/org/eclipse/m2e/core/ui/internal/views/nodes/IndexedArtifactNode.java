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

package org.eclipse.m2e.core.ui.internal.views.nodes;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import org.eclipse.m2e.core.internal.index.IndexedArtifact;
import org.eclipse.m2e.core.internal.index.IndexedArtifactFile;
import org.eclipse.m2e.core.ui.internal.MavenImages;
import org.eclipse.m2e.core.ui.internal.Messages;


/**
 * IndexedArtifactNode
 * 
 * @author dyocum
 */
@SuppressWarnings("restriction")
public class IndexedArtifactNode implements IMavenRepositoryNode, IArtifactNode {

  private IndexedArtifact artifact;

  private Object[] kids = null;

  public IndexedArtifactNode(IndexedArtifact artifact) {
    this.artifact = artifact;
  }

  public Object[] getChildren() {
    Set<IndexedArtifactFile> files = artifact.getFiles();
    if(files == null) {
      return new Object[0];
    }
    ArrayList<Object> fileList = new ArrayList<Object>();
    for(IndexedArtifactFile iaf : files) {
      fileList.add(new IndexedArtifactFileNode(iaf));
    }
    kids = fileList.toArray(new IndexedArtifactFileNode[fileList.size()]);
    return kids;
  }

  public String getName() {
    // return a.group + ":" + a.artifact;
    String pkg = artifact.getPackaging();
    if(pkg == null) {
      pkg = Messages.IndexedArtifactNode_no_pack;
    }
    return artifact.getArtifactId() + " - " + pkg; //$NON-NLS-1$
  }

  public boolean hasChildren() {
    //return kids != null && kids.length > 0;
    return true;
  }

  public Image getImage() {
    return MavenImages.IMG_JAR;
  }

  public String getDocumentKey() {
    return artifact.getArtifactId();
  }

  public boolean isUpdating() {
    return false;
  }

}
