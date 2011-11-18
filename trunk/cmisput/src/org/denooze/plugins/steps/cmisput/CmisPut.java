 /* Copyright (c) 2011 Gunter Rombauts.  All rights reserved. 
 * This software was developed by Gunter Rombauts and is provided under the terms 
 * of the GNU Lesser General Public License, Version 2.1. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.gnu.org/licenses/lgpl-2.1.txt. The Original Code is cmisput.  
 * The Initial Developer is Gunter Rombauts.
 *
 * Software distributed under the GNU Lesser Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.*/

package org.denooze.plugins.steps.cmisput;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.provider.local.LocalFile;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * Puts a document into a CMIS compatible content management repository.
 * 
 * @author Gunter Rombauts
 * @since 26-sep-2011
 */
public class CmisPut extends BaseStep implements StepInterface
{
	private static Class<?> PKG = CmisPutMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$
	private CmisPutMeta meta;
	private CmisPutData data;
	private CmisConnector	CmisConnector;
	
	public CmisPut(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans)
	{
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{ 
		meta=(CmisPutMeta)smi;
		data=(CmisPutData)sdi;
		Document CmisDoc;
		ObjectId CmisDocId;

		Object[] r=getRow();    // get row, set busy!
		if (r==null)  // no more input to be expected...
		{
			setOutputDone();
			if (CmisConnector.getSession()!=null) {
				CmisConnector.clearSession();
			}
			return false;
		}
		if (first) 
        {
			// get the RowMeta
			data.outputRowMeta = getInputRowMeta().clone();
			data.nrInfields = data.outputRowMeta.size();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			/* the getFields method in xxmeta add's the extra column field names */
			
			
			final Properties props = new Properties();
			props.setProperty("cms.url", environmentSubstitute(meta.getUrl()));
			props.setProperty("cms.repoId", environmentSubstitute(meta.getRepository()));
			props.setProperty("cms.password", environmentSubstitute(meta.getPassword()));
			props.setProperty("cms.username", environmentSubstitute(meta.getUsername()));
			CmisConnector = new CmisConnector(props);
			if ((meta.getUrl() != null) && (meta.getRepository() != null) && (meta.getPassword() != null) && (meta.getUsername() != null)) {			
				/* initialize connection */
				CmisConnector.initCmisSession();
				CmisConnector.setLocalNameSpaceFilter(meta.getLocalNameSpaceFilter());
				if (CmisConnector.getSession()==null){
					throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ConnectionFailed",CmisConnector.getMsgError())); //$NON-NLS-1$
				} else {
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.ConnectionOK")); //$NON-NLS-1$
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.cmisProductName",CmisConnector.getCmisProductName())); //$NON-NLS-1$
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.cmisVendor",CmisConnector.getCmisVendorName())); //$NON-NLS-1$
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.cmisVersionSupported",CmisConnector.getCmisVersionSupported())); //$NON-NLS-1$
					/* initialize versioning */
					if (meta.IsVersioned()) {
						if (meta.IsMajorVersion()){
							CmisConnector.setVersioningState(VersioningState.MAJOR);
							if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.VersioningState","MAJOR")); //$NON-NLS-1$
						} else {
							CmisConnector.setVersioningState(VersioningState.MINOR);
							if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.VersioningState","MINOR")); //$NON-NLS-1$
						}
					} else {
						CmisConnector.setVersioningState(VersioningState.NONE);
						if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.VersioningState","NONE")); //$NON-NLS-1$
					}
					/* if document has a fixed path - make sure this path exists */
					if (!CmisConnector.CreatePathIfNotExists(meta.getToPath(),"cmis:folder")){
						throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCreatingToPath",meta.getToPath(),CmisConnector.getMsgError())); //$NON-NLS-1$
					} else {
						if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CreateToPathOK",meta.getToPath())); //$NON-NLS-1$
					  }
				}				
			} else {
				throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.RepositoryLoginIncomplete")); //$NON-NLS-1$
			}

			if (checkrow(r,getTransMeta())){
				/* get index of fields in row layout.*/
				data.filenamefieldid = getInputRowMeta().indexOfValue(meta.getFilenamefield());
				if (data.filenamefieldid<0) 
        		{
        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.FilenameFieldNotFound",meta.getFilenamefield())); //$NON-NLS-1$
        		}
				data.documentfieldid = getInputRowMeta().indexOfValue(meta.getDocumentField());
				if (data.filenamefieldid<0) 
        		{
        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.DocumentFieldNotFound",meta.getDocumentField())); //$NON-NLS-1$
        		}
				//folders
				data.folderArgumentIndexes = new int[meta.getFolderArgumentField().length];
				for (int i=0;i<data.folderArgumentIndexes.length;i++) {
					data.folderArgumentIndexes[i] = getInputRowMeta().indexOfValue(meta.getFolderArgumentField()[i]);
	        		if (data.folderArgumentIndexes[i]<0) 
	        		{
	        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.FieldNotFound",meta.getFolderArgumentField()[i])); //$NON-NLS-1$
	        		}
	        		if ((meta.getFolderArgumentFolderType()[i]==null)||(meta.getFolderArgumentFolderType()[i].length()==0)){
	        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.FolderTypeNotFound")); //$NON-NLS-1$
	        		}
				}
				//aspects
				data.propertiesArgumentIndexes = new int[meta.getDocumentPropertyFieldName().length];
				for (int i=0;i<data.propertiesArgumentIndexes.length;i++) {
					data.propertiesArgumentIndexes[i] = getInputRowMeta().indexOfValue(meta.getDocumentPropertyFieldName()[i]);
	        		if (data.propertiesArgumentIndexes[i]<0) 
	        		{
	        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.FieldNotFound",meta.getDocumentPropertyFieldName()[i])); //$NON-NLS-1$
	        		}
	        		if ((meta.getDocumentPropertyFieldName()[i]==null)||(meta.getDocumentPropertyFieldName()[i].length()==0)){
	        			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.PropertyNotFound")); //$NON-NLS-1$
	        		}
				}
			}
			//TODO -get the predefined values for properties if any present, so a check can be done
			//GetPropertiesValueList();
			
			first=false;
        }
		
		if (meta.HasVariablePath()==true){/* if document has a variable path - make sure this path exists */
			if (!CmisConnector.CreateDynPathIfNotExists(meta.getToPath(),r,data.folderArgumentIndexes,meta.getFolderArgumentFolderType())){
				throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCreatingToPath",CmisConnector.getMsgError())); //$NON-NLS-1$
			} else {
				if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CreateToPathOK",CmisConnector.getLastCreatedDynPath())); //$NON-NLS-1$				
			  }
		}
		if (checkrow(r,getTransMeta())){
			/* add properties*/
			CmisConnector.removeDocumentproperties();
			if (meta.getDocumentPropertyFieldName()!=null) {
				for (int i=0;i<meta.getDocumentPropertyFieldName().length;i++)
				{
					CmisConnector.setDocumentproperty(meta.getDocumentPropertyName()[i],r[data.propertiesArgumentIndexes[i]].toString());
				}
			}
			/* create document*/
			String filenamefield = environmentSubstitute(r[data.filenamefieldid].toString());
			String documentfield = environmentSubstitute(r[data.documentfieldid].toString());
			String documentType = meta.getDocumentType();
			if (meta.getDocumentAspectName()!=null) {
				for (int i=0;i<meta.getDocumentAspectName().length;i++)
				{
					documentType = documentType + "," + meta.getDocumentAspectName()[i];
				}
			}
			if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.DocumentType",documentType)); //$NON-NLS-1$
			CmisDoc = CmisConnector.getDocumentByPath(filenamefield);
			if (CmisDoc!=null){
				/* Document exists */
				if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.DocExists",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
				/* Check if document is versionable */
				if (CmisConnector.DocumentIsVersionable(CmisDoc)){
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.DocIsVersionable",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
					if (!CmisConnector.DocumentIsCheckOut(CmisDoc)){
						CmisDocId = CmisConnector.CheckOutDocument(CmisDoc);
						if (CmisDocId!=null) {
							/* A versionable document needs to be checked out before a new version can be checked in */
							if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CheckOutDocumentOK",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
							if (CmisConnector.CheckInDocument(CmisDocId,documentType,filenamefield,documentfield,BaseMessages.getString(PKG, "CmisPut.Info.Comment"),getTransMeta())){
								if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CheckInDocumentOK",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
							} else {
								CmisConnector.CancelCheckOutDocument(CmisDocId);
								throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCheckingInDocument",CmisConnector.getLastCreatedDynPath(),filenamefield,CmisConnector.getMsgError())); //$NON-NLS-1$
							}
						} else {
							throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCheckingOutDocument",CmisConnector.getLastCreatedDynPath(),filenamefield,CmisConnector.getMsgError())); //$NON-NLS-1$
						}
					} else {
						throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.DocumentAlreadyCheckedOut",CmisConnector.getLastCreatedDynPath(),filenamefield,CmisConnector.getMsgError())); //$NON-NLS-1$
					}
				} else { /* document is not versionable */
					if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Info.DocIsNotVersionable",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
					
				}
			} else {
				/* document is new */
				if (!CmisConnector.CreateDocument(documentType,filenamefield,documentfield,getTransMeta())){
					if (meta.HasVariablePath()==true) {
						throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCreatingDocument",CmisConnector.getLastCreatedDynPath(),filenamefield,CmisConnector.getMsgError())); //$NON-NLS-1$
					} else {
						throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.ErrorCreatingDocument",meta.getToPath(),filenamefield,CmisConnector.getMsgError())); //$NON-NLS-1$
					}
				} else {
					if (meta.HasVariablePath()==true) {
						if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CreateDocumentOK",CmisConnector.getLastCreatedDynPath(),filenamefield)); //$NON-NLS-1$
					} else {
						if(log.isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPut.Exception.CreateDocumentOK",meta.getToPath(),filenamefield)); //$NON-NLS-1$	
					}
				  }
			}			
		}

		Object[] outputRowData = null;
		
		outputRowData = RowDataUtil.addValueData(r, getInputRowMeta().size(), CmisConnector.getCmsdocument().getId());
		putRow(data.outputRowMeta, outputRowData);     // copy row to possible alternate rowset(s).

        if (checkFeedback(getLinesRead())) 
        {
        	if(log.isBasic()) logBasic(BaseMessages.getString(PKG, "CmisPut.Log.LineNumber")+getLinesRead()); //$NON-NLS-1$
        }
			
		return true;
	}

	private boolean checkrow(Object[] r, TransMeta transmeta) throws KettleException {
		String sourcefile = environmentSubstitute((String)r[data.documentfieldid]);
		
		FileObject fileObject = KettleVFS.getFileObject(sourcefile, transmeta);
		if (!(fileObject instanceof LocalFile)) {
			// We can only use NIO on local files at the moment, so that's what we limit ourselves to.
			//
			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Log.OnlyLocalFilesAreSupported")); //$NON-NLS-1$
		}
		FileInputStream file;
		try {
			file = new FileInputStream(KettleVFS.getFilename(fileObject));
			file.close();
		} catch (FileNotFoundException e) {
			throw new KettleException(BaseMessages.getString(PKG, "CmisPut.Exception.InputFileNotFound",sourcefile)); //$NON-NLS-1$
		} catch (IOException e) {
			// should never occur
			e.printStackTrace();
		}		
		return true;
	}

	
}