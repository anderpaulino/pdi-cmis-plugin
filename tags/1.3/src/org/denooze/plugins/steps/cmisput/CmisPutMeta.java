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

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;
import org.pentaho.di.core.xml.XMLHandler;



/*
 * @author Gunter Rombauts
 * @since 26-sep-2011
 */

public class CmisPutMeta extends BaseStepMeta implements StepMetaInterface
{
	private static Class<?> PKG = CmisPutMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$
	
	/** standard / CMIS dialect to use */
	private String standard;
	
	/** URL / service to be called */
    private String  url;

    /** repository / repository to be used */
    private String  repository;
    
    /** username / username to be used */
    private String  username;
    
    /** username / username to be used */
    private String  password;
    
    /** BaseContentModel / ContentModel to be used */
    private String  basecontentmodel;
    
    /** DocumentType / DocumentType to be used */
    private String  documenttype;
    
    /** DocumentType / DocumentType to be used */
    private String  localnamespacefilter;

    /** documentfield / documentfield to be used */
    private String  documentfield;
    
    /** filenamefield / documentfield to be used */
    private String  filenamefield;
    
    /** topath / topath to be used */
    private String  topath;
    
    /** isversioned */
    private Boolean  isversioned;
    
    /** ismajorversion  */
    private Boolean  ismajorversion;
    
    /** isminorversion  */
    private Boolean  isminorversion;
    
    /** hasvariablepath  */
    private Boolean  hasvariablepath;

    /** function arguments : documentPropertyFieldName*/
    private String  documentAspectName[];
    
    /** function arguments : documentPropertyFieldName*/
    private String  documentPropertyFieldName[];
    
    /** function arguments : documentPropertyName*/
    private String  documentPropertyName[];

    /** function arguments : documentPropertyUpdatability*/
    private String  documentPropertyUpdatability[];

    /** function arguments : documentPropertyCardinality*/
    private String  documentPropertyCardinality[];

    /** function arguments : documentPropertyDisplayName*/
    private String  documentPropertyDisplayName[];

    /** function arguments : documentPropertyDocumentType*/
    private String  documentPropertyDocumentType[];

    /** function arguments : documentPropertyDataType*/
    private String  documentPropertyDataType[];
    
    /** function arguments : fieldname*/
    private String  folderArgumentField[];
    
    /** function arguments : property*/
    private String  folderArgumentFolderType[];
    
    /** topath / topath to be used */
    private String  cmisidfield;
    
    public CmisPutMeta()
	{
		super(); // allocate BaseStepMeta
	}
    /**
	 * @return the standard
	 */
	public String getStandard() {
		return standard;
	}
	/**
	 * @param standard the standard to set
	 */
	public void setStandard(String standard) {
		this.standard = standard;
	}
	
	/**
     * @return Returns the argument.
     */
    public String[] getDocumentAspectName()
    {
        return documentAspectName;
    }
    
    /**
     * @return Returns the argument.
     */
    public int getDocumentAspectNameLength()
    {
    	if (documentAspectName!=null){
    		return documentAspectName.length;
    	} else {
            return 0;
    	}
    }

    /**
     * @param argument The argument to set.
     */
    public void setDocumentAspectName(int i,String documentAspectName)
    {
        this.documentAspectName[i] = documentAspectName;
    }
	/**
     * @return Returns the argument.
     */
    public String[] getDocumentPropertyFieldName()
    {
        return documentPropertyFieldName;
    }
    
    /**
     * @return Returns the argument.
     */
    public int getDocumentPropertyFieldNameLength()
    {
    	if (documentPropertyFieldName!=null){
    		return documentPropertyFieldName.length;
    	} else {
            return 0;
    	}
    }

    /**
     * @param argument The argument to set.
     */
    public void setDocumentPropertyFieldName(int i,String documentPropertyFieldName)
    {
        this.documentPropertyFieldName[i] = documentPropertyFieldName;
    }
	/**
	 * @return the argumentProperty
	 */
    public String[] getDocumentPropertyName() {
		return documentPropertyName;
	}
	/**
	 * @param argumentProperty the argumentProperty to set
	 */
    public void setDocumentPropertyName(int i,String documentPropertyName) {
		this.documentPropertyName[i] = documentPropertyName;
	}

	/**
     * @return Returns the argument.
     */
    public String[] getFolderArgumentField()
    {
        return folderArgumentField;
    }

    /**
     * @return Returns the argument.
     */
    public int getFolderArgumentFieldLength()
    {
    	if (folderArgumentField!=null){
    		return folderArgumentField.length;
    	} else {
            return 0;
    	}
    }
    /**
     * @param argument The argument to set.
     */
    public void setFolderArgumentField(int i,String folderargument)
    {
        this.folderArgumentField[i] = folderargument;
    }
	/**
	 * @return the argumentProperty
	 */
	public String[] getFolderArgumentFolderType() {
		return folderArgumentFolderType;
	}
	/**
	 * @param argumentProperty the argumentProperty to set
	 */
	public void setFolderArgumentFolderType(int i,String folderArgumentFolderType) {
		this.folderArgumentFolderType[i] = folderArgumentFolderType;
	}
	/**
	 * @return the documentPropertyUpdatability
	 */
	public String[] getDocumentPropertyUpdatability() {
		return documentPropertyUpdatability;
	}
	/**
	 * @param documentPropertyUpdatability the documentPropertyUpdatability to set
	 */
	public void setDocumentPropertyUpdatability(int i,String documentPropertyUpdatability) {
		this.documentPropertyUpdatability[i] = documentPropertyUpdatability;
	}
	/**
	 * @return the documentPropertyCardinality
	 */
	public String[] getDocumentPropertyCardinality() {
		return documentPropertyCardinality;
	}
	/**
	 * @param documentPropertyCardinality the documentPropertyCardinality to set
	 */
	public void setDocumentPropertyCardinality(int i,String documentPropertyCardinality) {
		this.documentPropertyCardinality[i] = documentPropertyCardinality;
	}
	/**
	 * @return the documentPropertyDisplayName
	 */
	public String[] getDocumentPropertyDisplayName() {
		return documentPropertyDisplayName;
	}
	/**
	 * @param documentPropertyDisplayName the documentPropertyDisplayName to set
	 */
	public void setDocumentPropertyDisplayName(int i,String documentPropertyDisplayName) {
		this.documentPropertyDisplayName[i] = documentPropertyDisplayName;
	}
	/**
	 * @return the documentPropertyDocumentType
	 */
	public String[] getDocumentPropertyDocumentType() {
		return documentPropertyDocumentType;
	}
	/**
	 * @param documentPropertyDocumentType the documentPropertyDocumentType to set
	 */
	public void setDocumentPropertyDocumentType(int i,String documentPropertyDocumentType) {
		this.documentPropertyDocumentType[i] = documentPropertyDocumentType;
	}
	/**
	 * @return the documentPropertyDataType
	 */
	public String[] getDocumentPropertyDataType() {
		return documentPropertyDataType;
	}
	/**
	 * @param documentPropertyDataType the documentPropertyDataType to set
	 */
	public void setDocumentPropertyDataType(int i,String documentPropertyDataType) {
		this.documentPropertyDataType[i] = documentPropertyDataType;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the repository
	 */
	public String getRepository() {
		return repository;
	}
	/**
	 * @param repository the repository to set
	 */
	public void setRepository(String repository) {
		this.repository = repository;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the isversioned
	 */
	public Boolean IsVersioned() {
		return isversioned;
	}
	/**
	 * @param isversioned the isversioned to set
	 */
	public void setIsVersioned(Boolean isversioned) {
		this.isversioned = isversioned;
	}
	/**
	 * @return the ismajorversion
	 */
	public Boolean IsMajorVersion() {
		return ismajorversion;
	}
	/**
	 * @param ismajorversion the ismajorversion to set
	 */
	public void setIsMajorVersion(Boolean ismajorversion) {
		this.ismajorversion = ismajorversion;
	}
	/**
	 * @return the isminorversion
	 */
	public Boolean IsMinorVersion() {
		return isminorversion;
	}
	/**
	 * @param isminorversion the isminorversion to set
	 */
	public void setIsMinorVersion(Boolean isminorversion) {
		this.isminorversion = isminorversion;
	}
	/**
	 * @return the hasVariablePath
	 */
	public Boolean HasVariablePath() {
		return hasvariablepath;
	}
	/**
	 * @param hasVariablePath the hasVariablePath to set
	 */
	public void setHasVariablePath(Boolean hasvariablepath) {
		this.hasvariablepath = hasvariablepath;
	}
	/**
	 * @return the toPath
	 */
	public String getToPath() {
		return topath;
	}
	/**
	 * @param toPath the toPath to set
	 */
	public void setToPath(String topath) {
		this.topath = topath;
	}
	/**
	 * @return the documentField
	 */
	public String getDocumentField() {
		return documentfield;
	}
	/**
	 * @param documentField the documentField to set
	 */
	public void setDocumentField(String documentfield) {
		this.documentfield = documentfield;
	}
	/**
	 * @return the filenamefield
	 */
	public String getFilenamefield() {
		return filenamefield;
	}
	/**
	 * @param filenamefield the filenamefield to set
	 */
	public void setFilenamefield(String filenamefield) {
		this.filenamefield = filenamefield;
	}
	/* */
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
		throws KettleXMLException
	{
		readData(stepnode);
	}

	public Object clone()
	{
		Object retval = super.clone();
		return retval;
	}
	
	public String getXML()
    {
        StringBuffer retval = new StringBuffer(300);

        retval.append("    ").append(XMLHandler.addTagValue("url", url)); //$NON-NLS-1$ //$NON-NLS-2$
        retval.append("    " + XMLHandler.addTagValue("repository",  repository));
        retval.append("    " + XMLHandler.addTagValue("standard",  standard));
        retval.append("    " + XMLHandler.addTagValue("username",  username));
        /* TODO encrypt password before saving it to the file*/
        retval.append("    " + XMLHandler.addTagValue("password", password));
        retval.append("    " + XMLHandler.addTagValue("basecontentmodel",  basecontentmodel));
        retval.append("    " + XMLHandler.addTagValue("documenttype", documenttype));
        retval.append("    " + XMLHandler.addTagValue("localnamespacefilter", localnamespacefilter));
        retval.append("    " + XMLHandler.addTagValue("isversioned", isversioned));
        retval.append("    " + XMLHandler.addTagValue("ismajorversion", ismajorversion));
        retval.append("    " + XMLHandler.addTagValue("isminorversion", isminorversion));
        retval.append("    " + XMLHandler.addTagValue("hasvariablepath", hasvariablepath));
        retval.append("    " + XMLHandler.addTagValue("topath", topath));
        retval.append("    " + XMLHandler.addTagValue("documentfield", documentfield));
        retval.append("    " + XMLHandler.addTagValue("filenamefield",filenamefield ));
        retval.append("    " + XMLHandler.addTagValue("cmisidfield",cmisidfield ));
        
        retval.append("    <metadata>").append(Const.CR); //$NON-NLS-1$
        //folders
        for (int i = 0; i < folderArgumentField.length; i++)
        {
            retval.append("      <folderarg>").append(Const.CR); //$NON-NLS-1$
            retval.append("        ").append(XMLHandler.addTagValue("folderfield", folderArgumentField[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("foldertype", folderArgumentFolderType[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("      </folderarg>").append(Const.CR); //$NON-NLS-1$
        }
        // Aspects
        for (int i = 0; i < documentAspectName.length; i++)
        {
            retval.append("      <aspectarg>").append(Const.CR); //$NON-NLS-1$
            retval.append("        ").append(XMLHandler.addTagValue("aspectname", documentAspectName[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("      </aspectarg>").append(Const.CR); //$NON-NLS-1$
        }
        //properties
        for (int i = 0; i < documentPropertyFieldName.length; i++)
        {
            retval.append("      <documentarg>").append(Const.CR); //$NON-NLS-1$
            retval.append("        ").append(XMLHandler.addTagValue("propertyfieldname", documentPropertyFieldName[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("propertyname", documentPropertyName[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("documentPropertyupdatability", getDocumentPropertyUpdatability()[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("documentPropertyCardinality", getDocumentPropertyCardinality()[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("documentPropertyDisplayName", getDocumentPropertyDisplayName()[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("documentPropertyDocumentType", getDocumentPropertyDocumentType()[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("        ").append(XMLHandler.addTagValue("documentPropertyDataType", getDocumentPropertyDataType()[i])); //$NON-NLS-1$ //$NON-NLS-2$
            retval.append("      </documentarg>").append(Const.CR); //$NON-NLS-1$
        }

        retval.append("    </metadata>").append(Const.CR); //$NON-NLS-1$

        return retval.toString();
    }
	
	private void readData(Node stepnode)
	{
        int nrargs,nrfolderargs,nraspectargs;

        url = XMLHandler.getTagValue(stepnode, "url"); //$NON-NLS-1$
        repository = XMLHandler.getTagValue(stepnode, "repository");
        standard = XMLHandler.getTagValue(stepnode, "standard");
        username = XMLHandler.getTagValue(stepnode, "username");
        /* TODO decrypt password before saving it to the file*/
        password = XMLHandler.getTagValue(stepnode, "password");
        basecontentmodel = XMLHandler.getTagValue(stepnode, "basecontentmodel");
        documenttype = XMLHandler.getTagValue(stepnode, "documenttype");
        localnamespacefilter = XMLHandler.getTagValue(stepnode, "localnamespacefilter");
        isversioned = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "isversioned"));
        ismajorversion = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "ismajorversion"));
        isminorversion = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "isminorversion"));
        hasvariablepath = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "hasvariablepath"));
        topath = XMLHandler.getTagValue(stepnode, "topath");
        documentfield = XMLHandler.getTagValue(stepnode, "documentfield");
        filenamefield = XMLHandler.getTagValue(stepnode, "filenamefield");
        cmisidfield = XMLHandler.getTagValue(stepnode, "cmisidfield");
        
        
        Node lookup = XMLHandler.getSubNode(stepnode, "metadata"); //$NON-NLS-1$
        //folders
        nrfolderargs = XMLHandler.countNodes(lookup, "folderarg"); //$NON-NLS-1$
        allocatefolder(nrfolderargs);
        for (int i = 0; i < nrfolderargs; i++)
        {
            Node anode = XMLHandler.getSubNodeByNr(lookup, "folderarg", i); //$NON-NLS-1$

            folderArgumentField[i] = XMLHandler.getTagValue(anode, "folderfield"); //$NON-NLS-1$
            folderArgumentFolderType[i] = XMLHandler.getTagValue(anode, "foldertype"); //$NON-NLS-1$
        }
        //Aspects
        nraspectargs = XMLHandler.countNodes(lookup, "aspectarg"); //$NON-NLS-1$
        allocateaspect(nraspectargs);
        for (int i = 0; i < nraspectargs; i++)
        {
            Node anode = XMLHandler.getSubNodeByNr(lookup, "aspectarg", i); //$NON-NLS-1$

            documentAspectName[i] = XMLHandler.getTagValue(anode, "aspectname"); //$NON-NLS-1$
        }
        //properties
        nrargs = XMLHandler.countNodes(lookup, "documentarg"); //$NON-NLS-1$
        allocate(nrargs);
        for (int i = 0; i < nrargs; i++)
        {
            Node anode = XMLHandler.getSubNodeByNr(lookup, "documentarg", i); //$NON-NLS-1$

            documentPropertyFieldName[i] = XMLHandler.getTagValue(anode, "propertyfieldname"); //$NON-NLS-1$
            documentPropertyName[i] = XMLHandler.getTagValue(anode, "propertyname"); //$NON-NLS-1$
            getDocumentPropertyUpdatability()[i] = XMLHandler.getTagValue(anode, "documentPropertyupdatability"); //$NON-NLS-1$
            getDocumentPropertyCardinality()[i] = XMLHandler.getTagValue(anode, "documentPropertyCardinality"); //$NON-NLS-1$
            getDocumentPropertyDisplayName()[i] = XMLHandler.getTagValue(anode, "documentPropertyDisplayName"); //$NON-NLS-1$
            getDocumentPropertyDocumentType()[i] = XMLHandler.getTagValue(anode, "documentPropertyDocumentType"); //$NON-NLS-1$
            getDocumentPropertyDataType()[i] = XMLHandler.getTagValue(anode, "documentPropertyDataType"); //$NON-NLS-1$
        }

    
	}

	public void setDefault()
	{
		int nrargs;
		nrargs = 0;
    
		allocate(nrargs);
		setBaseContentModel("cmis:document");
		isversioned = false;
        ismajorversion = false;
        isminorversion = false;
        hasvariablepath = false;
        cmisidfield = "cmisid";
	}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
		throws KettleException
	{
        /* TODO implement*/
	}
	
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
		throws KettleException
	{
        /* TODO implement*/
	}
	
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) throws KettleStepException {
        
    	ValueMetaInterface v=new ValueMeta(this.getCmisidfield(), ValueMetaInterface.TYPE_STRING);
        v.setOrigin(name);
        row.addValueMeta( v );
    }

	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepinfo, RowMetaInterface prev, String input[], String output[], RowMetaInterface info)
	{
		CheckResult cr;
		if (prev==null || prev.size()==0)
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString(PKG, "CmisPutMeta.CheckResult.NotReceivingFields"), stepinfo); //$NON-NLS-1$
			remarks.add(cr);
		}
		else
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "CmisPutMeta.CheckResult.StepRecevingData",prev.size()+""), stepinfo); //$NON-NLS-1$ //$NON-NLS-2$
			remarks.add(cr);
		}
		
		// See if we have input streams leading to this step!
		if (input.length>0)
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "CmisPutMeta.CheckResult.StepRecevingData2"), stepinfo); //$NON-NLS-1$
			remarks.add(cr);
		}
		else
		{
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "CmisPutMeta.CheckResult.NoInputReceivedFromOtherSteps"), stepinfo); //$NON-NLS-1$
			remarks.add(cr);
		}
	}
	
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr, Trans trans)
	{
		return new CmisPut(stepMeta, stepDataInterface, cnr, tr, trans);
	}
	
	public StepDataInterface getStepData()
	{
		return new CmisPutData();
	}

	public void allocate(int nrargs)
    {
		documentPropertyFieldName = new String[nrargs];
		documentPropertyName = new String[nrargs];
		documentPropertyUpdatability = new String[nrargs];
		documentPropertyCardinality = new String[nrargs];
		documentPropertyDisplayName = new String[nrargs];
		documentPropertyDocumentType = new String[nrargs];
		documentPropertyDataType = new String[nrargs];
    }

	public void allocateaspect(int nrargs)
    {
		documentAspectName = new String[nrargs];
    }
	
	public void allocatefolder(int nrargs)
    {
        folderArgumentField = new String[nrargs];
        folderArgumentFolderType = new String[nrargs];
    }
	/**
	 * @return the baseContentModel
	 */
	public String getBaseContentModel() {
		return basecontentmodel;
	}
	/**
	 * @param baseContentModel the baseContentModel to set
	 */
	public void setBaseContentModel(String basecontentmodel) {
		this.basecontentmodel = basecontentmodel;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documenttype;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documenttype) {
		this.documenttype = documenttype;
	}
	/**
	 * @return the localNameSpaceFilter
	 */
	public String getLocalNameSpaceFilter() {
		return localnamespacefilter;
	}
	/**
	 * @param localNameSpaceFilter the localNameSpaceFilter to set
	 */
	public void setLocalNameSpaceFilter(String localnamespacefilter) {
		this.localnamespacefilter = localnamespacefilter;
	}
	/**
	 * @return the cmisidfield
	 */
	public String getCmisidfield() {
		return cmisidfield;
	}
	/**
	 * @param cmisidfield the cmisidfield to set
	 */
	public void setCmisidfield(String cmisidfield) {
		this.cmisidfield = cmisidfield;
	}
}
