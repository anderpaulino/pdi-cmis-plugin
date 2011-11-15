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

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

//import org.alfresco.cmis.client.AlfrescoDocument;
//import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.spi.CmisBinding;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ChoiceImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;


/**
 * @author Gunter
 *
 */
public class CmisConnector implements Cloneable
{
	
	private Properties cmsProperties;
    private Session session;
    private Iterable<Repository> repolist;
    private String msgError;
    private String localNameSpaceFilter;
    private AlfrescoFolder parentFolder;
    private AlfrescoDocument cmsdocument;
    private String lastCreatedDynPath;
    private String cmisProductName;
    private String cmisVendorName;
    private String cmisVersionSupported;
    private VersioningState VersioningState;
    private Map<String, Object> documentproperties = new HashMap<String, Object>();
    /**
	 * 
	 */
    public CmisConnector(final Properties properties) {
	this.cmsProperties = properties;
	this.session =  null;
	this.setRepolist(null);
//	this.session = initCmisSession();
    }

	/* initialize CMIS session */
	public void initCmisSession() {

		Session sess;
		setMsgError(null);
			try {
				// Default factory implementation of client runtime.
				final SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
				final Map<String, String> parameter = new HashMap<String, String>();

				parameter.put(SessionParameter.USER, this.cmsProperties.getProperty("cms.username"));
				parameter.put(SessionParameter.PASSWORD, this.cmsProperties.getProperty("cms.password"));
				parameter.put(SessionParameter.ATOMPUB_URL, this.cmsProperties.getProperty("cms.url"));
				parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
				parameter.put(SessionParameter.REPOSITORY_ID, this.cmsProperties.getProperty("cms.repoId"));
				parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

			    CmisBindingFactory factory = CmisBindingFactory.newInstance();
			    CmisBinding binding = factory.createCmisAtomPubBinding(parameter);
				RepositoryInfo repositoryInfo = binding.getRepositoryService().getRepositoryInfo(this.cmsProperties.getProperty("cms.repoId") , null);
			    
				//get information about the system
				setCmisProductName(repositoryInfo.getProductName());
				setCmisVendorName(repositoryInfo.getVendorName());
				setCmisVersionSupported(repositoryInfo.getCmisVersionSupported());

				sess = sessionFactory.createSession(parameter);

				// Turn off the session cache completely
				sess.getDefaultContext().setCacheEnabled(false);
				
				
			} catch (Exception e) {
				setMsgError(e.getMessage());
				sess = null;
			}			
			this.session = sess;
	    }
	/* Get list of repositories */
	public void getRepositories(CCombo wRepository) {

		Iterable<Repository> repolist;
		setMsgError(null);
			try {
				// Default factory implementation of client runtime.
				final SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
				final Map<String, String> parameter = new HashMap<String, String>();

				parameter.put(SessionParameter.USER, this.cmsProperties.getProperty("cms.username"));
				parameter.put(SessionParameter.PASSWORD, this.cmsProperties.getProperty("cms.password"));
				parameter.put(SessionParameter.ATOMPUB_URL, this.cmsProperties.getProperty("cms.url"));
				parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
				parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

				repolist = sessionFactory.getRepositories(parameter);	
				for (Repository repoID : repolist) {
					wRepository.add(repoID.getId());
				}
								
			} catch (Exception e) {
				setMsgError(e.getMessage());
				repolist = null;
			}			
			this.setRepolist(repolist);
	    }
	/**
	 * @return the cmsProperties
	 */
	public Properties getCmsProperties() {
		return cmsProperties;
	}

	/**
	 * @param cmsProperties the cmsProperties to set
	 */
	public void setCmsProperties(Properties cmsProperties) {
		this.cmsProperties = cmsProperties;
	}

	/**
	 * @return the session
	 */
	 public Session getSession() {
		 return this.session;
	    }
	 /**
	 * @return the session
	 */
	 public void clearSession() {
		 this.session.clear();
	    }
	/**
	 * @return the repolist
	 */
	public Iterable<Repository> getRepolist() {
		return repolist;
	}

	/**
	 * @param repolist the repolist to set
	 */
	public void setRepolist(Iterable<Repository> repolist) {
		this.repolist = repolist;
	}

	/**
	 * @return the msgError
	 */
	public String getMsgError() {
		return msgError;
	}

	/**
	 * @param msgError the msgError to set
	 */
	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}
	/**
	 * @return the localNameSpaceFilter
	 */
	public String getLocalNameSpaceFilter() {
		return localNameSpaceFilter;
	}

	/**
	 * @param localNameSpaceFilter the localNameSpaceFilter to set
	 */
	public void setLocalNameSpaceFilter(String localNameSpaceFilter) {
		this.localNameSpaceFilter = localNameSpaceFilter;
	}

	/**
	 * @return the cmisProductName
	 */
	public String getCmisProductName() {
		return cmisProductName;
	}

	/**
	 * @param cmisProductName the cmisProductName to set
	 */
	public void setCmisProductName(String cmisProductName) {
		this.cmisProductName = cmisProductName;
	}

	/**
	 * @return the cmisVendorName
	 */
	public String getCmisVendorName() {
		return cmisVendorName;
	}

	/**
	 * @param cmisVendorName the cmisVendorName to set
	 */
	public void setCmisVendorName(String cmisVendorName) {
		this.cmisVendorName = cmisVendorName;
	}

	/**
	 * @return the cmisVersionSupported
	 */
	public String getCmisVersionSupported() {
		return cmisVersionSupported;
	}

	/**
	 * @param cmisVersionSupported the cmisVersionSupported to set
	 */
	public void setCmisVersionSupported(String cmisVersionSupported) {
		this.cmisVersionSupported = cmisVersionSupported;
	}

	/**
	 * @return the versioningState
	 */
	public VersioningState getVersioningState() {
		return VersioningState;
	}

	/**
	 * @param versioningState the versioningState to set
	 */
	public void setVersioningState(VersioningState versioningState) {
		VersioningState = versioningState;
	}

	/**
	 * @return the documentproperties
	 */
	public Map<String, Object> getDocumentproperties() {
		return documentproperties;
	}

	/**
	 * @param documentproperties the documentproperties to set
	 */
	public void setDocumentproperty(String key, Object value) {
		this.documentproperties.put(key, value);
	}

	/**
	 * @param documentproperties the documentproperties to set
	 */
	public void removeDocumentproperties() {
		this.documentproperties.clear();
	}

	/**
	 * @return the parentFolder
	 */
	public AlfrescoFolder getParentFolder() {
		return parentFolder;
	}

	/**
	 * @param parentFolder the parentFolder to set
	 */
//	public void setParentFolder(AlfrescoFolder parentFolder) {
//		this.parentFolder = parentFolder;
//	}

	/**
	 * @return the lastCreatedDynPath
	 */
	public String getLastCreatedDynPath() {
		return lastCreatedDynPath;
	}

	/**
	 * @param lastCreatedDynPath the lastCreatedDynPath to set
	 */
	public void setLastCreatedDynPath(String lastCreatedDynPath) {
		this.lastCreatedDynPath = lastCreatedDynPath;
	}

	/**
	 * 
	 */
	public Document getDocumentByPath(String cmsfilename){
		Document Doc = null;
		String documentPath = getLastCreatedDynPath() + "/" + cmsfilename;
		try {
			Doc  = (Document) session.getObjectByPath(documentPath);
		} catch (CmisObjectNotFoundException e) {
			setMsgError(e.getMessage());
		}
		return Doc;
	}
	
	public ItemIterable<QueryResult> getDocumentByQuery(String query){
		query = "SELECT * FROM qp:quotationspace where cmis:name = 'Quotation Test 3'";
		ItemIterable<QueryResult> results = session.query(query, false);
		return results;
	}
	
	public void GetDocumentTypeList(CCombo wDocumentType, TextVar wBaseContentModel){
		ItemIterable<ObjectType> childDocType = session.getTypeChildren(wBaseContentModel.getText(), true);
		for (ObjectType objectType : childDocType) {
			String localNameSpace = objectType.getLocalNamespace();
			if (!localNameSpaceFilter.isEmpty()){
				if (localNameSpace.contains(localNameSpaceFilter))
				{
//					wDocumentType.add(objectType.getDisplayName()+" ("+objectType.getId()+")");
					wDocumentType.add(objectType.getId());
					GetChildDocumentTypeList(objectType.getId(),wDocumentType);
				}
			} else {
//				wDocumentType.add(objectType.getDisplayName()+" ("+objectType.getId()+")");
				wDocumentType.add(objectType.getId());
				GetChildDocumentTypeList(objectType.getId(),wDocumentType);
			}
		}
	}

	private void GetChildDocumentTypeList(String parentDocumentType,CCombo wDocumentType){
		/*TODO - test it and integrate in ui*/
		ItemIterable<ObjectType> childDocType = session.getTypeChildren(parentDocumentType, true);
		for (ObjectType objectType : childDocType) {
			String localNameSpace = objectType.getLocalNamespace();
			if (!localNameSpaceFilter.isEmpty()){
				if (localNameSpace.contains(localNameSpaceFilter))
				{
//					wDocumentType.add(objectType.getDisplayName()+" ("+objectType.getId()+")");
					wDocumentType.add(objectType.getId());
					GetChildDocumentTypeList(objectType.getId(),wDocumentType);
				}
			} else {
//				wDocumentType.add(objectType.getDisplayName()+" ("+objectType.getId()+")");
				Map<String, PropertyDefinition<?>> propertyDefinitions = objectType.getPropertyDefinitions();
				
				wDocumentType.add(objectType.getId());
				GetChildDocumentTypeList(objectType.getId(),wDocumentType);
			}
		}
	}

	public void GetFolderTypeList(CCombo wDocumentType, String cmisBaseFolderType){
		ItemIterable<ObjectType> childDocType = session.getTypeChildren(cmisBaseFolderType, true);
		wDocumentType.add(cmisBaseFolderType);
		for (ObjectType objectType : childDocType) {
			String localNameSpace = objectType.getLocalNamespace();
			if (!localNameSpaceFilter.isEmpty()){
				if (localNameSpace.contains(localNameSpaceFilter))
				{
					wDocumentType.add(objectType.getId());
					GetFolderTypeList(wDocumentType,objectType.getId());
				}
			} else {
				wDocumentType.add(objectType.getId());
				GetFolderTypeList(wDocumentType,objectType.getId());
			}
		}
	}
	
	public Boolean CreateDynPathIfNotExists(String topath, Object[] r, int[] folderArgumentIndexes, String[] object_type_id){

	    final Map<String, Object> properties = new HashMap<String, Object>();
		String CurrentFolder;
		
		if (topath == null) {
			topath = "/";
		}
		
		if (topath.endsWith("/")) {
			topath = topath.substring(0, topath.length()-1);
		}

		parentFolder = (AlfrescoFolder) getSession().getObjectByPath(topath);
		
		for (int i=0;i<folderArgumentIndexes.length;i++) {
			CurrentFolder = (String) r[folderArgumentIndexes[i]];
			topath = topath + "/" + CurrentFolder.trim();

			AlfrescoFolder Folder = null;
			try {
				Folder = (AlfrescoFolder) getSession().getObjectByPath(topath);
			} catch (CmisObjectNotFoundException e) {
				try {
					Folder = createFolder(parentFolder,CurrentFolder,properties,object_type_id[i]);
				} catch (Exception e1) {
					setMsgError(e.getMessage());
					return false;
				}
			}
			parentFolder = Folder;
			setLastCreatedDynPath(topath);
		}
		return true;
	}
	
	public Boolean CreatePathIfNotExists(String topath, String object_type_id){
	    final Map<String, Object> properties = new HashMap<String, Object>();
		parentFolder = (AlfrescoFolder) getSession().getRootFolder();
		topath.replace("\"", "/");
		StringTokenizer tokenizer = new StringTokenizer(topath, "/"); 
		String CurrentFolder;
		
		topath = null;
		while (tokenizer.hasMoreTokens()){
			CurrentFolder = tokenizer.nextToken();
			if (topath != null) {
				topath = topath + "/" + CurrentFolder.trim();
			} else {
				topath = "/" + CurrentFolder;
			}
			AlfrescoFolder Folder = null;
			try {
				Folder = (AlfrescoFolder) getSession().getObjectByPath(topath);
			} catch (CmisObjectNotFoundException e) {
				try {
					Folder = createFolder(parentFolder,CurrentFolder,properties,object_type_id);
				} catch (Exception e1) {
					setMsgError(e.getMessage());
					return false;
				}
			}			
			parentFolder = Folder;
			setLastCreatedDynPath(topath);
		}
		return true;
	}
	
	public Boolean CreateDocument(String documenttype,String cmsfilename,String sourcefile){

	    final Map<String, Object> properties = getDocumentproperties();
		FileInputStream file;
		if (sourcefile==null){
			return false;
		}
		try {
			file = new FileInputStream(sourcefile);
		} catch (FileNotFoundException e) {
			setMsgError(e.getMessage());
			return false;
		}
		
		ContentStream content = new ContentStreamImpl(cmsfilename, null, DetectMimeType(sourcefile), file);
	    properties.put(PropertyIds.OBJECT_TYPE_ID, documenttype);
	    setCmsdocument(createDocument(getParentFolder(),content,properties));
	    if (getCmsdocument()==null){
			return false;
	    } else {
			return true;
	    }
	}
	
	private void checkTypeSet(final Map<String, Object> properties) {
		if (!properties.containsKey(PropertyIds.OBJECT_TYPE_ID)) {
		    throw new Error("Object type id is not set.");
		}
	}
	
	public AlfrescoDocument createDocument(final Folder parent, final ContentStream content, final Map<String, Object> properties) {
		Document newDocument = null;
		properties.put(PropertyIds.NAME, content.getFileName());
		checkTypeSet(properties);
		
		try {
			newDocument = parent.createDocument(properties, content, getVersioningState());
		} catch (Exception e) {
			setMsgError(e.getMessage());
		}
		return (AlfrescoDocument) newDocument;
	}
	
	public AlfrescoFolder createFolder(final Folder parent, final String name, final Map<String, Object> properties, String object_type_id) {
	    properties.put(PropertyIds.OBJECT_TYPE_ID, object_type_id);
		properties.put(PropertyIds.NAME, name);

		final Folder newFolder = parent.createFolder(properties);

		return (AlfrescoFolder) newFolder;
	}

	/**
	 * @return the cmsdocument
	 */
	public AlfrescoDocument getCmsdocument() {
		return cmsdocument;
	}

	/**
	 * @param cmsdocument the cmsdocument to set
	 */
	public void setCmsdocument(AlfrescoDocument cmsdocument) {
		this.cmsdocument = cmsdocument;
	}
	/**
	 * @return if the document is versionable
	 */
	public Boolean DocumentIsVersionable(Document CmisDoc) {
		/*TODO implement*/
		return true;
	}
	/**
	 * @return if the document is versionable
	 */
	public Boolean DocumentIsCheckOut(Document CmisDoc) {
		/*TODO implement check */
		if (CmisDoc.isVersionSeriesCheckedOut()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @return checked out document
	 */
	public ObjectId CheckOutDocument(Document CmisDoc) {
		ObjectId id = CmisDoc.checkOut();	
		return id;
	}

	public Boolean CheckInDocument(ObjectId CmisDocId,String documenttype,String cmsfilename,String sourcefile,String comment) {

		ObjectId checkedInDocId; 

	    final Map<String, Object> properties = getDocumentproperties();
		FileInputStream file = null;
		
		if (sourcefile==null){
			return false;
		}
		try {
			file = new FileInputStream(sourcefile);
			AlfrescoDocument checkedOutDocument = (AlfrescoDocument) session.getObject(CmisDocId);
			ContentStream content = new ContentStreamImpl(cmsfilename, null, DetectMimeType(sourcefile), file);
		    properties.put(PropertyIds.OBJECT_TYPE_ID, documenttype);
		    if (this.VersioningState.equals(org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR)) {
				checkedInDocId = checkedOutDocument.checkIn(true, properties, content, comment);
		    } else {
				checkedInDocId = checkedOutDocument.checkIn(false, properties, content, comment);
		    }
		    setCmsdocument((AlfrescoDocument) session.getObject(checkedInDocId)); 
		} catch (FileNotFoundException e) {
			setMsgError(e.getMessage());
			return false;
		}  finally {
			try {
				file.close();
			} catch (IOException e) {
				/* not interested in error*/
			}
		}
	    if (getCmsdocument()==null){
			return false;
	    } else {
			return true;
	    }
	}
	
	private String DetectMimeType(String sourcefile){

		/* TODO detect mime type */
		FileInputStream is = null;
		Metadata metadata = null;
		    try {
		      File f = new File(sourcefile);
		      is = new FileInputStream(f);

		      ContentHandler contenthandler = new BodyContentHandler();
		      metadata = new Metadata();
		      metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
		      Parser parser = new AutoDetectParser();
		      // OOXMLParser parser = new OOXMLParser();
		      parser.parse(is, contenthandler, metadata,null);
//		      System.out.println("Mime: " + metadata.get(Metadata.CONTENT_TYPE));
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		    finally {
		        if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						/* not interested in error*/
					}
		    }
		    
		    if (is != null) {
		    	return "plain/text";
		    } else {
		    	return metadata.get(Metadata.CONTENT_TYPE);
		    }
	}
	

	public void setCmisDialogProperties(TableView wMetaDataList,String documenttype) {
		int i=0;
		//not possible for properties in an aspect
		wMetaDataList.removeAll();
		while ((documenttype!=null) && (!documenttype.isEmpty())){
			ItemIterable<ObjectType> types = session.getTypeChildren(documenttype, true); 
			for( ObjectType type2 : types) { 
				Map<String, PropertyDefinition<?>> properties = type2.getPropertyDefinitions();
				for (Map.Entry<String,PropertyDefinition<?>> entry : properties.entrySet()) {
					PropertyDefinition<?> propertyDefinition = entry.getValue();
					if(!(propertyDefinition.getId().equals(PropertyIds.NAME)) && !(propertyDefinition.getId().equals(PropertyIds.OBJECT_TYPE_ID))){
						propertyDefinition.getUpdatability();
						if(propertyDefinition.getUpdatability()!=Updatability.READONLY){
							/* skip name, because already defined elsewhere & object id */
							wMetaDataList.table.setItemCount(i+1);
							TableItem item = wMetaDataList.table.getItem(i);
							i+=1;
							item.setText(0, Integer.toString(i));
							item.setText(2, Const.NVL(propertyDefinition.getId(), ""));
							item.setText(3, Const.NVL(propertyDefinition.getUpdatability().name(), ""));
							item.setText(4, Const.NVL(propertyDefinition.getCardinality().name(), ""));
							item.setText(5, Const.NVL(propertyDefinition.getLocalName(), ""));
							item.setText(6, Const.NVL(documenttype, ""));
						}
					}
					
				} 					     
			}
			/* get the parent document type */
			documenttype = session.getTypeDefinition(documenttype).getParentType().getId();
			if (documenttype.equals("cmis:document")){
				documenttype = null;
			}
		}	
	}
}
