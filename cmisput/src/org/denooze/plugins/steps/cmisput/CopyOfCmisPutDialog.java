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

/*
 * Created on 01-11-2011
 *
 */

package org.denooze.plugins.steps.cmisput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.chicagometallic.plugins.steps.cmisget.CmisConnector;

public class CopyOfCmisPutDialog extends BaseStepDialog implements StepDialogInterface
{
	private static Class<?> PKG = CmisPutMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

	private boolean   gotPreviousFields=false;

	private CTabFolder   wTabFolder;
    private FormData     fdTabFolder;
    
    private CTabItem     wContentTab,wDocumentTab,wPropertiesTab;
    private FormData     fdContentComp,fdDocumentComp,fdPropertiesComp; 
    
    private Composite	 wPropertiesComp;
    
    private Label        wlStandard;
    private CCombo       wStandard;
    private FormData     fdlStandard, fdStandard;
    
    private Label        wlDocumentType;
    private CCombo       wDocumentType;
    private FormData     fdlDocumentType, fdDocumentType;

    private Label        wlUrl;
    private TextVar       wUrl;
    private FormData     fdlUrl, fdUrl;
    
    private Label        wlBaseContentModel;
    private TextVar      wBaseContentModel;
    private FormData     fdlBaseContentModel, fdBaseContentModel;

    private Label        wlLocalNameSpaceFilter;
    private TextVar      wLocalNameSpaceFilter;
    private FormData     fdlLocalNameSpaceFilter, fdLocalNameSpaceFilter;
    
    private Label        wlRepository;
    private CCombo       wRepository;
    private FormData     fdlRepository, fdRepository;
    
    private Label        wlUsername;
    private TextVar      wUsername;
    private FormData     fdlUsername, fdUsername;

    private Label        wlPassword;
    private TextVar      wPassword;
    private FormData     fdlPassword, fdPassword;

	private Label        wlMetaDataList;
	private TableView    wMetaDataList;
	private FormData     fdlMetaDataList, fdMetaDataList;

	private Label        wlIsVersioned;
	private Button    	 wIsVersioned;
	private FormData     fdlIsVersioned, fdIsVersioned;

	private Label        wlIsMajorVersion;
	private Button    	 wIsMajorVersion;
	private FormData     fdlIsMajorVersion, fdIsMajorVersion;

	private Label        wlIsMinorVersion;
	private Button    	 wIsMinorVersion;
	private FormData     fdlIsMinorVersion, fdIsMinorVersion;

    private Label        wlToPath;
    private TextVar      wToPath;
    private FormData     fdlToPath, fdToPath;
    
	private Button		 wAbout,wTest;
	private Listener	 lsAbout,lsTest;

	private Label        wlHasVariablePath;
	private Button    	 wHasVariablePath;
	private FormData     fdlHasVariablePath, fdHasVariablePath;

    private Label        wlDocumentField;
    private CCombo       wDocumentField;
    private FormData     fdlDocumentField, fdDocumentField;

    private Label        wlCmisIdField;
    private TextVar      wCmisIdField;
    private FormData     fdlCmisIdField, fdCmisIdField;

    private Label        wlFileNameField;
    private CCombo       wFileNameField;
    private FormData     fdlFileNameField, fdFileNameField;

	private Group 		 wDocTypeGroup,wConnectionGroup,wRepositoryGroup,wDocumentGroup,wVersioningGroup;
	private FormData 	 fdDocTypeGroup,fdConnectionGroup,fdRepositoryGroup,fdDocumentGroup,fdVersioningGroup;

	private Label        wlDynDirStruct;
	private TableView    wDynDirStruct;
	private FormData     fdlDynDirStruct, fdDynDirStruct;
	
	private Label		 wlGetDocumentProperties; 
	private Button		 wGetDocumentProperties;
	private Listener	 lsGetDocumentProperties;
	
    private Map<String, Integer> inputFields;
	
    private ColumnInfo[] colinf,dyndirstructcolinf;
	
	private CmisPutMeta input;
	
	private CmisConnector CmisConnector;

	public CopyOfCmisPutDialog(Shell parent, Object in, TransMeta tr, String sname)
	{
		super(parent, (BaseStepMeta)in, tr, sname);
		input=(CmisPutMeta)in;
        inputFields = new HashMap<String, Integer>();
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
 		props.setLook(shell);
 		setShellImage(shell, input);
        
		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "CmisPutDialog.Shell.Title")); //$NON-NLS-1$
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "CmisPutDialog.Stepname.Label")); //$NON-NLS-1$
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, -margin);
		fdlStepname.top  = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		Control lastControl = wStepname;
		
		// Content tab
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
        props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
        
        //////////////////////////
        // START OF CONTENT TAB///
        ///
        wContentTab=new CTabItem(wTabFolder, SWT.NONE);
        wContentTab.setText(BaseMessages.getString(PKG, "CmisPutDialog.ContentTab.TabTitle"));
        

        FormLayout contentLayout = new FormLayout ();
        contentLayout.marginWidth  = 3;
        contentLayout.marginHeight = 3;
        
        Composite wContentComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wContentComp);
        wContentComp.setLayout(contentLayout);

        fdContentComp = new FormData();
        fdContentComp.left  = new FormAttachment(0, 0);
        fdContentComp.top   = new FormAttachment(0, 0);
        fdContentComp.right = new FormAttachment(100, 0);
        fdContentComp.bottom= new FormAttachment(100, 0);
        wContentComp.setLayoutData(fdContentComp);

        wContentComp.layout();
        wContentTab.setControl(wContentComp);
        
        /* Group Connection Type */
        wConnectionGroup = new Group(wContentComp, SWT.SHADOW_NONE);
		props.setLook(wConnectionGroup);
		wConnectionGroup.setText(BaseMessages.getString(PKG, "CmisPutDialog.ConnectionGroup.GroupTitle"));
		
		FormLayout groupConnection = new FormLayout();
		groupConnection.marginWidth = 10;
		groupConnection.marginHeight = 0;
		wConnectionGroup.setLayout(groupConnection);

        fdConnectionGroup = new FormData();
    	fdConnectionGroup.left = new FormAttachment(0, margin);
    	fdConnectionGroup.top = new FormAttachment(wPassword, margin);
    	fdConnectionGroup.right = new FormAttachment(100, -margin);
    	wConnectionGroup.setLayoutData(fdConnectionGroup);
    	
        /* CMIS standard to use*/
        wlStandard=new Label(wConnectionGroup, SWT.RIGHT);
        wlStandard.setText(BaseMessages.getString(PKG, "CmisPutDialog.Standard.Label"));
        props.setLook(wlStandard);
        fdlStandard=new FormData();
        fdlStandard.left = new FormAttachment(0, 0);
        fdlStandard.top  = new FormAttachment(null, margin);
        fdlStandard.right= new FormAttachment(middle, -margin);
        wlStandard.setLayoutData(fdlStandard);
        wStandard=new CCombo(wConnectionGroup, SWT.BORDER | SWT.READ_ONLY);
        wStandard.setEditable(true);
        props.setLook(wStandard);
        wStandard.addModifyListener(lsMod);
        fdStandard=new FormData();
        fdStandard.left = new FormAttachment(middle, 0);
        fdStandard.top  = new FormAttachment(null, margin);
        fdStandard.right= new FormAttachment(100, 0);
        wStandard.setLayoutData(fdStandard);
        wStandard.add(BaseMessages.getString(PKG, "CmisPutDialog.Standard.NONE"));
        wStandard.add(BaseMessages.getString(PKG, "CmisPutDialog.Standard.ALFRESCO"));
        wStandard.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
            	setCmisDialogProperties();
            }
        });
        
        /* CMIS URL to use*/
        wlUrl=new Label(wConnectionGroup, SWT.RIGHT);
        wlUrl.setText(BaseMessages.getString(PKG, "CmisPutDialog.Url.Label"));
        props.setLook(wlUrl);
        fdlUrl=new FormData();
        fdlUrl.left = new FormAttachment(0, 0);
        fdlUrl.top  = new FormAttachment(wStandard, margin);
        fdlUrl.right= new FormAttachment(middle, -margin);
        wlUrl.setLayoutData(fdlUrl);
        wUrl=new TextVar(transMeta, wConnectionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wUrl.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.URL.Tooltip")); //$NON-NLS-1$
 		props.setLook(wUrl);
		wUrl.addModifyListener(lsMod);
        fdUrl=new FormData();
        fdUrl.left = new FormAttachment(middle, 0);
        fdUrl.top  = new FormAttachment(wStandard, margin);
        fdUrl.right= new FormAttachment(100, 0);
        wUrl.setLayoutData(fdUrl);
        
        /* CMIS username to use*/
        wlUsername=new Label(wConnectionGroup, SWT.RIGHT);
        wlUsername.setText(BaseMessages.getString(PKG, "CmisPutDialog.Username.Label"));
        props.setLook(wlUsername);
        fdlUsername=new FormData();
        fdlUsername.left = new FormAttachment(0, 0);
        fdlUsername.top  = new FormAttachment(wUrl, margin);
        fdlUsername.right= new FormAttachment(middle, -margin);
        wlUsername.setLayoutData(fdlUsername);
        wUsername=new TextVar(transMeta, wConnectionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wUsername.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.Username.Tooltip")); //$NON-NLS-1$
        props.setLook(wUsername);
        wUsername.addModifyListener(lsMod);
        fdUsername=new FormData();
        fdUsername.left = new FormAttachment(middle, 0);
        fdUsername.top  = new FormAttachment(wUrl, margin);
        fdUsername.right= new FormAttachment(100, 0);
        wUsername.setLayoutData(fdUsername);
        
        /* CMIS password to use*/
        wlPassword=new Label(wConnectionGroup, SWT.RIGHT);
        wlPassword.setText(BaseMessages.getString(PKG, "CmisPutDialog.Password.Label"));
        props.setLook(wlPassword);
        fdlPassword=new FormData();
        fdlPassword.left = new FormAttachment(0, 0);
        fdlPassword.top  = new FormAttachment(wUsername, margin);
        fdlPassword.right= new FormAttachment(middle, -margin);
        wlPassword.setLayoutData(fdlPassword);
        wPassword=new TextVar(transMeta, wConnectionGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wPassword.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.Password.Tooltip")); //$NON-NLS-1$
        wPassword.setEchoChar('*');
        props.setLook(wPassword);
        wPassword.addModifyListener(lsMod);
        fdPassword=new FormData();
        fdPassword.left = new FormAttachment(middle, 0);
        fdPassword.top  = new FormAttachment(wUsername, margin);
        fdPassword.right= new FormAttachment(100, 0);
        wPassword.setLayoutData(fdPassword);
        

        /* Group repository */
        wRepositoryGroup = new Group(wContentComp, SWT.SHADOW_NONE);
		props.setLook(wRepositoryGroup);
		wRepositoryGroup.setText(BaseMessages.getString(PKG, "CmisPutDialog.RepositoryGroup.GroupTitle"));
		
		FormLayout groupRepository = new FormLayout();
		groupRepository.marginWidth = 10;
		groupRepository.marginHeight = 0;
		wRepositoryGroup.setLayout(groupRepository);

        fdRepositoryGroup = new FormData();
    	fdRepositoryGroup.left = new FormAttachment(0, margin);
    	fdRepositoryGroup.top = new FormAttachment(wConnectionGroup, margin);
    	fdRepositoryGroup.right = new FormAttachment(100, -margin);
    	wRepositoryGroup.setLayoutData(fdRepositoryGroup);
    	
        /* repository to use*/
        wlRepository=new Label(wRepositoryGroup, SWT.RIGHT);
        wlRepository.setText(BaseMessages.getString(PKG, "CmisPutDialog.Repository.Label"));
        props.setLook(wlRepository);
        fdlRepository=new FormData();
        fdlRepository.left = new FormAttachment(0, 0);
        fdlRepository.top  = new FormAttachment(wRepositoryGroup, margin);
        fdlRepository.right= new FormAttachment(middle, -margin);
        wlRepository.setLayoutData(fdlRepository);
        wRepository=new CCombo(wRepositoryGroup, SWT.BORDER | SWT.READ_ONLY);
        wRepository.setToolTipText(BaseMessages.getString(PKG, "HTTPDialog.Repository.Tooltip")); //$NON-NLS-1$
        props.setLook(wRepository);
        wRepository.addModifyListener(lsMod);
        fdRepository=new FormData();
        fdRepository.left = new FormAttachment(middle, 0);
        fdRepository.top  = new FormAttachment(wRepositoryGroup, margin);
        fdRepository.right= new FormAttachment(100, 0);
        wRepository.setLayoutData(fdRepository);
        wRepository.addFocusListener(new FocusListener()
        {
            public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
        
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                wRepository.removeAll();
                CmisConnector.getRepositories(wRepository);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
		//////////////////////////
		// START OF DOCUMENT TAB///
		///
		wDocumentTab=new CTabItem(wTabFolder, SWT.NONE);
		wDocumentTab.setText(BaseMessages.getString(PKG, "CmisPutDialog.DocumentTab.TabTitle"));
		
		
		FormLayout DocumentLayout = new FormLayout ();
		DocumentLayout.marginWidth  = 3;
		DocumentLayout.marginHeight = 3;
		
		Composite wDocumentComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wDocumentComp);
		wDocumentComp.setLayout(DocumentLayout);
		
        fdDocumentComp = new FormData();
        fdDocumentComp.left  = new FormAttachment(0, 0);
        fdDocumentComp.top   = new FormAttachment(0, 0);
        fdDocumentComp.right = new FormAttachment(100, 0);
        fdDocumentComp.bottom= new FormAttachment(100, 0);
        wDocumentComp.setLayoutData(fdDocumentComp);

        wDocumentComp.layout();
        wDocumentTab.setControl(wDocumentComp);

        /* Group Document Type */
        wDocTypeGroup = new Group(wDocumentComp, SWT.SHADOW_NONE);
		props.setLook(wDocTypeGroup);
		wDocTypeGroup.setText(BaseMessages.getString(PKG, "CmisPutDialog.DocTypeGroup.GroupTitle"));
		
		FormLayout groupDocType = new FormLayout();
		groupDocType.marginWidth = 10;
		groupDocType.marginHeight = 0;
		wDocTypeGroup.setLayout(groupDocType);

        fdDocTypeGroup = new FormData();
    	fdDocTypeGroup.left = new FormAttachment(0, margin);
    	fdDocTypeGroup.top = new FormAttachment(wPassword, margin);
    	fdDocTypeGroup.right = new FormAttachment(100, -margin);
    	wDocTypeGroup.setLayoutData(fdDocTypeGroup);
    	
        /* CMIS BaseContentModel to use*/
        wlBaseContentModel=new Label(wDocTypeGroup, SWT.RIGHT);
        wlBaseContentModel.setText(BaseMessages.getString(PKG, "CmisPutDialog.BaseContentModel.Label"));
        props.setLook(wlBaseContentModel);
        fdlBaseContentModel=new FormData();
        fdlBaseContentModel.left = new FormAttachment(0, 0);
        fdlBaseContentModel.top  = new FormAttachment(wDocTypeGroup, margin);
        fdlBaseContentModel.right= new FormAttachment(middle, -margin);
        wlBaseContentModel.setLayoutData(fdlBaseContentModel);
        wBaseContentModel=new TextVar(transMeta, wDocTypeGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wBaseContentModel.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.BaseContentModel.Tooltip")); //$NON-NLS-1$
 		props.setLook(wBaseContentModel);
		wBaseContentModel.addModifyListener(lsMod);
        fdBaseContentModel=new FormData();
        fdBaseContentModel.left = new FormAttachment(middle, 0);
        fdBaseContentModel.top  = new FormAttachment(wDocTypeGroup, margin);
        fdBaseContentModel.right= new FormAttachment(100, 0);
        wBaseContentModel.setLayoutData(fdBaseContentModel);

        /* CMIS LocalNameSpaceFilter to use*/
        wlLocalNameSpaceFilter=new Label(wDocTypeGroup, SWT.RIGHT);
        wlLocalNameSpaceFilter.setText(BaseMessages.getString(PKG, "CmisPutDialog.LocalNameSpaceFilter.Label"));
        props.setLook(wlLocalNameSpaceFilter);
        fdlLocalNameSpaceFilter=new FormData();
        fdlLocalNameSpaceFilter.left = new FormAttachment(0, 0);
        fdlLocalNameSpaceFilter.top  = new FormAttachment(wBaseContentModel, margin);
        fdlLocalNameSpaceFilter.right= new FormAttachment(middle, -margin);
        wlLocalNameSpaceFilter.setLayoutData(fdlLocalNameSpaceFilter);
        wLocalNameSpaceFilter=new TextVar(transMeta, wDocTypeGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wLocalNameSpaceFilter.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.LocalNameSpaceFilter.Tooltip")); //$NON-NLS-1$
 		props.setLook(wLocalNameSpaceFilter);
		wLocalNameSpaceFilter.addModifyListener(lsMod);
        fdLocalNameSpaceFilter=new FormData();
        fdLocalNameSpaceFilter.left = new FormAttachment(middle, 0);
        fdLocalNameSpaceFilter.top  = new FormAttachment(wBaseContentModel, margin);
        fdLocalNameSpaceFilter.right= new FormAttachment(100, 0);
        wLocalNameSpaceFilter.setLayoutData(fdLocalNameSpaceFilter);
        
        /* CMIS ContentModel to use*/
        wlDocumentType=new Label(wDocTypeGroup, SWT.RIGHT);
        wlDocumentType.setText(BaseMessages.getString(PKG, "CmisPutDialog.DocumentType.Label"));
        props.setLook(wlDocumentType);
        fdlDocumentType=new FormData();
        fdlDocumentType.left = new FormAttachment(0, 0);
        fdlDocumentType.top  = new FormAttachment(wLocalNameSpaceFilter, margin);
        fdlDocumentType.right= new FormAttachment(middle, -margin);
        wlDocumentType.setLayoutData(fdlDocumentType);
        wDocumentType=new CCombo(wDocTypeGroup, SWT.BORDER | SWT.READ_ONLY);
        wDocumentType.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.DocumentType.Tooltip")); //$NON-NLS-1$
        wDocumentType.setEditable(true);
        props.setLook(wDocumentType);
        wDocumentType.addModifyListener(lsMod);
        fdDocumentType=new FormData();
        fdDocumentType.left = new FormAttachment(middle, 0);
        fdDocumentType.top  = new FormAttachment(wLocalNameSpaceFilter, margin);
        fdDocumentType.right= new FormAttachment(100, 0);
        wDocumentType.setLayoutData(fdDocumentType);
        wDocumentType.addFocusListener(new FocusListener()
        {
            public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
        
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                wDocumentType.removeAll();
    			CmisConnector.setLocalNameSpaceFilter(wLocalNameSpaceFilter.getText());
                CmisConnector.GetDocumentTypeList(wDocumentType,wBaseContentModel);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
        /* Group Document */
        wDocumentGroup = new Group(wDocumentComp, SWT.SHADOW_NONE);
		props.setLook(wDocumentGroup);
		wDocumentGroup.setText(BaseMessages.getString(PKG, "CmisPutDialog.Document.GroupTitle"));
		
		FormLayout groupDocument = new FormLayout();
		groupDocument.marginWidth = 10;
		groupDocument.marginHeight = 0;
		wDocumentGroup.setLayout(groupDocument);

        fdDocumentGroup = new FormData();
    	fdDocumentGroup.left = new FormAttachment(0, margin);
    	fdDocumentGroup.top = new FormAttachment(wDocTypeGroup, margin);
    	fdDocumentGroup.right = new FormAttachment(100, -margin);
    	wDocumentGroup.setLayoutData(fdDocumentGroup);
    	

        /* Field containing the source document*/
        wlDocumentField=new Label(wDocumentGroup, SWT.RIGHT);
        wlDocumentField.setText(BaseMessages.getString(PKG, "CmisPutDialog.DocumentField.Label"));
        props.setLook(wlDocumentField);
        fdlDocumentField=new FormData();
        fdlDocumentField.left = new FormAttachment(0, 0);
        fdlDocumentField.top  = new FormAttachment(wDocumentGroup, margin);
        fdlDocumentField.right= new FormAttachment(middle, -margin);
        wlDocumentField.setLayoutData(fdlDocumentField);
        wDocumentField=new CCombo(wDocumentGroup, SWT.BORDER | SWT.READ_ONLY);
        wDocumentField.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.DocumentField.Tooltip")); //$NON-NLS-1$
        wDocumentField.setEditable(true);
        props.setLook(wDocumentField);
        wDocumentField.addModifyListener(lsMod);
        fdDocumentField=new FormData();
        fdDocumentField.left = new FormAttachment(middle, 0);
        fdDocumentField.top  = new FormAttachment(wDocumentGroup, margin);
        fdDocumentField.right= new FormAttachment(100, 0);
        wDocumentField.setLayoutData(fdDocumentField);
        wDocumentField.addFocusListener(new FocusListener()
        {
            public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
        
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                getFieldNames(wDocumentField);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
        
        /* Field containing the cmis id of the document in the cms*/
        wlCmisIdField=new Label(wDocumentGroup, SWT.RIGHT);
        wlCmisIdField.setText(BaseMessages.getString(PKG, "CmisPutDialog.CmisIdField.Label"));
        props.setLook(wlCmisIdField);
        fdlCmisIdField=new FormData();
        fdlCmisIdField.left = new FormAttachment(0, 0);
        fdlCmisIdField.top  = new FormAttachment(wDocumentField, margin);
        fdlCmisIdField.right= new FormAttachment(middle, -margin);
        wlCmisIdField.setLayoutData(fdlCmisIdField);
        wCmisIdField=new TextVar(transMeta, wDocumentGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wCmisIdField.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.CmisIdField.Tooltip")); //$NON-NLS-1$
        wCmisIdField.setEditable(true);
        props.setLook(wCmisIdField);
        wCmisIdField.addModifyListener(lsMod);
        fdCmisIdField=new FormData();
        fdCmisIdField.left = new FormAttachment(middle, 0);
        fdCmisIdField.top  = new FormAttachment(wDocumentField, margin);
        fdCmisIdField.right= new FormAttachment(100, 0);
        wCmisIdField.setLayoutData(fdCmisIdField);
        
        /* CMIS Has Variable Path to use*/
        wlHasVariablePath=new Label(wDocumentGroup, SWT.RIGHT);
        wlHasVariablePath.setText(BaseMessages.getString(PKG, "CmisPutDialog.HasVariablePath.Label"));
        props.setLook(wlHasVariablePath);
        fdlHasVariablePath=new FormData();
        fdlHasVariablePath.left = new FormAttachment(0, 0);
        fdlHasVariablePath.top  = new FormAttachment(wCmisIdField, margin);
        fdlHasVariablePath.right= new FormAttachment(middle, -margin);
        wlHasVariablePath.setLayoutData(fdlHasVariablePath);
        wHasVariablePath=new Button(wDocumentGroup, SWT.CHECK );
        wHasVariablePath.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.HasVariablePath.Tooltip")); //$NON-NLS-1$
        props.setLook(wHasVariablePath);
        fdHasVariablePath=new FormData();
        fdHasVariablePath.left = new FormAttachment(middle, 0);
        fdHasVariablePath.top  = new FormAttachment(wCmisIdField, margin);
        fdHasVariablePath.right= new FormAttachment(100, 0);
        wHasVariablePath.setLayoutData(fdHasVariablePath);
        wHasVariablePath.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	SetVariablePathLogic();
                    input.setChanged();
                }
            }
        );
        
        /* CMIS ToPath to use*/
        wlToPath=new Label(wDocumentGroup, SWT.RIGHT);
        wlToPath.setText(BaseMessages.getString(PKG, "CmisPutDialog.ToPath.Label"));
        props.setLook(wlToPath);
        fdlToPath=new FormData();
        fdlToPath.left = new FormAttachment(0, 0);
        fdlToPath.top  = new FormAttachment(wHasVariablePath, margin);
        fdlToPath.right= new FormAttachment(middle, -margin);
        wlToPath.setLayoutData(fdlToPath);
        wToPath=new TextVar(transMeta, wDocumentGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wToPath.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.ToPath.Tooltip")); //$NON-NLS-1$
 		props.setLook(wToPath);
		wToPath.addModifyListener(lsMod);
        fdToPath=new FormData();
        fdToPath.left = new FormAttachment(middle, 0);
        fdToPath.top  = new FormAttachment(wHasVariablePath, margin);
        fdToPath.right= new FormAttachment(100, 0);
        wToPath.setLayoutData(fdToPath);
       
        /* Table for dynamic directory structure */
        wlDynDirStruct=new Label(wDocumentGroup, SWT.RIGHT);
        wlDynDirStruct.setText(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.Label")); //$NON-NLS-1$
        props.setLook(wlDynDirStruct);
        fdlDynDirStruct=new FormData();
        fdlDynDirStruct.left = new FormAttachment(0, 0);
        fdlDynDirStruct.top  = new FormAttachment(wToPath, margin);
        fdlDynDirStruct.right= new FormAttachment(middle, -margin);
        wlDynDirStruct.setLayoutData(fdlDynDirStruct);
         
//     	final int FieldsRows=input.getArgumentField().length;
     	final int DynDirStructFieldsRows=0;
     	
     	dyndirstructcolinf=new ColumnInfo[] { 
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.DirName"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false),
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.DirType"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "cmis:folder" }, false),
     	  /*TODO make list of dirTypes dynamic.*/
        };
     		
     	wDynDirStruct=new TableView(transMeta, wDocumentGroup, 
     							  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
     							  dyndirstructcolinf, 
     							  DynDirStructFieldsRows,  
     							  lsMod,
     							  props
     							  );

     	fdDynDirStruct=new FormData();
     	fdDynDirStruct.left  = new FormAttachment(middle, 0);
     	fdDynDirStruct.top   = new FormAttachment(wToPath, margin);
     	fdDynDirStruct.right = new FormAttachment(100, 0);
     	fdDynDirStruct.bottom= new FormAttachment(wToPath, 150);
     	wDynDirStruct.setLayoutData(fdDynDirStruct);
//        final Color GREEN = new Color(display, 0, 255, 0);
//        wDynDirStruct.setBackground(GREEN);

        /* Field for document*/
        wlFileNameField=new Label(wDocumentGroup, SWT.RIGHT);
        wlFileNameField.setText(BaseMessages.getString(PKG, "CmisPutDialog.FileNameField.Label"));
        props.setLook(wlFileNameField);
        fdlFileNameField=new FormData();
        fdlFileNameField.left = new FormAttachment(0, 0);
        fdlFileNameField.top  = new FormAttachment(wDynDirStruct, margin);
        fdlFileNameField.right= new FormAttachment(middle, -margin);
        wlFileNameField.setLayoutData(fdlFileNameField);
        wFileNameField=new CCombo(wDocumentGroup, SWT.BORDER | SWT.READ_ONLY);
        wFileNameField.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.FileNameField.Tooltip")); //$NON-NLS-1$
        wFileNameField.setEditable(true);
        props.setLook(wFileNameField);
        wFileNameField.addModifyListener(lsMod);
        fdFileNameField=new FormData();
        fdFileNameField.left = new FormAttachment(middle, 0);
        fdFileNameField.top  = new FormAttachment(wDynDirStruct, margin);
        fdFileNameField.right= new FormAttachment(100, 0);
        wFileNameField.setLayoutData(fdFileNameField);
        wFileNameField.addFocusListener(new FocusListener()
        {
            public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
        
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                getFieldNames(wFileNameField);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );

        /* Group Versioning */
        wVersioningGroup = new Group(wDocumentComp, SWT.SHADOW_NONE);
		props.setLook(wVersioningGroup);
		wVersioningGroup.setText(BaseMessages.getString(PKG, "CmisPutDialog.Versioning.GroupTitle"));
		
		FormLayout groupVersioning = new FormLayout();
		groupVersioning.marginWidth = 10;
		groupVersioning.marginHeight = 0;
		wVersioningGroup.setLayout(groupVersioning);

        fdVersioningGroup = new FormData();
    	fdVersioningGroup.left = new FormAttachment(0, margin);
    	fdVersioningGroup.top = new FormAttachment(wDocumentGroup, margin);
    	fdVersioningGroup.right = new FormAttachment(100, -margin);
    	wVersioningGroup.setLayoutData(fdVersioningGroup);
    	
        /* CMIS Is versioned to use*/
        wlIsVersioned=new Label(wVersioningGroup, SWT.RIGHT);
        wlIsVersioned.setText(BaseMessages.getString(PKG, "CmisPutDialog.IsVersioned.Label"));
        props.setLook(wlIsVersioned);
        fdlIsVersioned=new FormData();
        fdlIsVersioned.left = new FormAttachment(0, 0);
        fdlIsVersioned.top  = new FormAttachment(wVersioningGroup, margin);
        fdlIsVersioned.right= new FormAttachment(middle, -margin);
        wlIsVersioned.setLayoutData(fdlIsVersioned);
        wIsVersioned=new Button(wVersioningGroup, SWT.CHECK );
        wIsVersioned.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.IsVersioned.Tooltip")); //$NON-NLS-1$
        props.setLook(wIsVersioned);
        fdIsVersioned=new FormData();
        fdIsVersioned.left = new FormAttachment(middle, 0);
        fdIsVersioned.top  = new FormAttachment(wVersioningGroup, margin);
        fdIsVersioned.right= new FormAttachment(100, 0);
        wIsVersioned.setLayoutData(fdIsVersioned);
        wIsVersioned.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	SetVersionLogic();
                    input.setChanged();
                }
            }
        );
        

        /* CMIS Is major version*/
        wlIsMajorVersion=new Label(wVersioningGroup, SWT.RIGHT);
        wlIsMajorVersion.setText(BaseMessages.getString(PKG, "CmisPutDialog.IsMajorVersion.Label"));
    	wlIsMajorVersion.setEnabled(false);
        props.setLook(wlIsMajorVersion);
        fdlIsMajorVersion=new FormData();
        fdlIsMajorVersion.left = new FormAttachment(0, 0);
        fdlIsMajorVersion.top  = new FormAttachment(wIsVersioned, margin);
        fdlIsMajorVersion.right= new FormAttachment(middle, -margin);
        wlIsMajorVersion.setLayoutData(fdlIsMajorVersion);
        wIsMajorVersion=new Button(wVersioningGroup, SWT.CHECK );
        wIsMajorVersion.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.IsMajorVersion.Tooltip")); //$NON-NLS-1$
        props.setLook(wIsMajorVersion);
        fdIsMajorVersion=new FormData();
        fdIsMajorVersion.left = new FormAttachment(middle, 0);
        fdIsMajorVersion.top  = new FormAttachment(wIsVersioned, margin);
        fdIsMajorVersion.right= new FormAttachment(middle+5, 0);
        wIsMajorVersion.setLayoutData(fdIsMajorVersion);
    	wIsMajorVersion.setEnabled(false);
        wIsMajorVersion.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	if (wIsMajorVersion.getSelection()==true){
                		wIsMinorVersion.setSelection(false);
                	} else {
                		wIsMinorVersion.setSelection(true);
                	}
                    input.setChanged();
                }
            }
        );

        /* CMIS Is minor version*/
        wlIsMinorVersion=new Label(wVersioningGroup, SWT.RIGHT);
        wlIsMinorVersion.setText(BaseMessages.getString(PKG, "CmisPutDialog.IsMinorVersion.Label"));
    	wlIsMinorVersion.setEnabled(false);
        props.setLook(wlIsMinorVersion);
        fdlIsMinorVersion=new FormData();
        fdlIsMinorVersion.left = new FormAttachment(middle+5, margin);
        fdlIsMinorVersion.top  = new FormAttachment(wIsVersioned, margin);
        fdlIsMinorVersion.right= new FormAttachment(middle*3/2, -margin);
        wlIsMinorVersion.setLayoutData(fdlIsMinorVersion);
        wIsMinorVersion=new Button(wVersioningGroup, SWT.CHECK );
        wIsMinorVersion.setToolTipText(BaseMessages.getString(PKG, "CmisPutDialog.IsMinorVersion.Tooltip")); //$NON-NLS-1$
        props.setLook(wIsMinorVersion);
        fdIsMinorVersion=new FormData();
        fdIsMinorVersion.left = new FormAttachment(middle*3/2, 0);
        fdIsMinorVersion.top  = new FormAttachment(wIsVersioned, margin);
        fdIsMinorVersion.right= new FormAttachment(100, 0);
        wIsMinorVersion.setLayoutData(fdIsMinorVersion);
        wIsMinorVersion.setEnabled(false);
        wIsMinorVersion.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	if (wIsMinorVersion.getSelection()==true){
                		wIsMajorVersion.setSelection(false);
                	} else {
                		wIsMajorVersion.setSelection(true);
                	}
                    input.setChanged();
                }
            }
        );
  
        /* add test button */
 		wTest=new Button(wContentComp, SWT.PUSH);
 		wTest.setText(BaseMessages.getString(PKG, "CmisPutDialog.Button.TestConnection")); //$NON-NLS-1$
 		setButtonPositions(new Button[] { wTest }, margin, null);
        
        /*TODO Add tool tips.*/
        
        
     // Properties tab...
     	wPropertiesTab = new CTabItem(wTabFolder, SWT.NONE);
     	wPropertiesTab.setText(BaseMessages.getString(PKG, "CmisPutDialog.PropertiesTab.Title"));
     	
     	FormLayout addLayout = new FormLayout ();
     	addLayout.marginWidth  = Const.FORM_MARGIN;
     	addLayout.marginHeight = Const.FORM_MARGIN;
     	
     	wPropertiesComp = new Composite(wTabFolder, SWT.NONE);
     	wPropertiesComp.setLayout(addLayout);
        props.setLook(wPropertiesComp);

        wlMetaDataList=new Label(wPropertiesComp, SWT.NONE);
        wlMetaDataList.setText(BaseMessages.getString(PKG, "CmisPutDialog.Mapping.Label")); //$NON-NLS-1$
        props.setLook(wlMetaDataList);
        fdlMetaDataList=new FormData();
        fdlMetaDataList.left = new FormAttachment(0, 0);
        fdlMetaDataList.top  = new FormAttachment(lastControl, margin);
        wlMetaDataList.setLayoutData(fdlMetaDataList);
        lastControl = wlMetaDataList;
             
     	wGetDocumentProperties  =new Button(wPropertiesComp, SWT.PUSH);
     	wGetDocumentProperties.setText(BaseMessages.getString(PKG, "CmisPutDialog.GetPropertyList.Button")); //$NON-NLS-1$
     	FormData fdGetDocumentProperties  = new FormData();
     	fdGetDocumentProperties.top = new FormAttachment(wlMetaDataList, margin);
     	fdGetDocumentProperties.right = new FormAttachment(100, 0);
     	wGetDocumentProperties.setLayoutData(fdGetDocumentProperties);
		lsGetDocumentProperties    = new Listener() { public void handleEvent(Event e) { GetDocumentProperties(shell);     } };
		wGetDocumentProperties.addListener(SWT.Selection, lsGetDocumentProperties );
     		
//     	final int FieldsRows=input.getArgumentField().length;
     	final int FieldsRows=0;
     	
     	colinf=new ColumnInfo[] { 
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyFieldName"),      ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false),
//     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.ColumnInfo.Property"),  ColumnInfo.COLUMN_TYPE_TEXT,   false), //$NON-NLS-1$
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyName"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true),
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyUpdatability"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true),
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyCardinality"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true),
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyDisplayName"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true),
     	  new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.MetaDataList.ColumnInfo.PropertyDocumentType"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true),
        };
     		
     	wMetaDataList=new TableView(transMeta, wPropertiesComp,
     							  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
     							  colinf, 
     							  FieldsRows,  
     							  lsMod,
     							  props
     							  );

     	fdMetaDataList=new FormData();
     	fdMetaDataList.left  = new FormAttachment(0, 0);
     	fdMetaDataList.top   = new FormAttachment(wlMetaDataList, margin);
     	fdMetaDataList.right = new FormAttachment(wGetDocumentProperties, -margin);
     	fdMetaDataList.bottom= new FormAttachment(wlMetaDataList, 200);
     	wMetaDataList.setLayoutData(fdMetaDataList);
     		


//     		wlHeaders = new Label(wPropertiesComp,SWT.NONE);
//     		wlHeaders.setText(BaseMessages.getString(PKG, "CmisPutDialog.Headers.Label"));
//     		props.setLook(wlHeaders);
//     		fdlHeaders = new FormData();
//     		fdlHeaders.left = new FormAttachment(0,0);
//     		fdlHeaders.top = new FormAttachment(wMetaDataList, margin);
//     		wlHeaders.setLayoutData(fdlHeaders);
//     		
//     		final int HeadersRows = input.getHeaderParameter().length;
//     		
//     		colinfHeaders = new ColumnInfo[] {
//     		      new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.ColumnInfo.Field"),ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false),
//     		      new ColumnInfo(BaseMessages.getString(PKG, "CmisPutDialog.ColumnInfo.Header"),ColumnInfo.COLUMN_TYPE_TEXT,   false), //$NON-NLS-1$
//     		};
//     		colinfHeaders[1].setUsingVariables(true);
//     		wHeaders = new TableView(transMeta, wPropertiesComp, 
//                 SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, 
//                 colinfHeaders, 
//                 HeadersRows,  
//                 lsMod,
//                 props
//                 );
//
//     		wGetHeaders=new Button(wPropertiesComp, SWT.PUSH);
//     		wGetHeaders.setText(BaseMessages.getString(PKG, "CmisPutDialog.GetHeaders.Button")); //$NON-NLS-1$
//           FormData fdGetHeaders = new FormData();
//           fdGetHeaders.top = new FormAttachment(wlHeaders, margin);
//           fdGetHeaders.right = new FormAttachment(100, 0);
//           wGetHeaders.setLayoutData(fdGetHeaders);
//     		
//           fdHeaders=new FormData();
//           fdHeaders.left  = new FormAttachment(0, 0);
//           fdHeaders.top   = new FormAttachment(wlHeaders, margin);
//           fdHeaders.right = new FormAttachment(wGetHeaders, -margin);
//           fdHeaders.bottom= new FormAttachment(100, -margin);
//           wHeaders.setLayoutData(fdHeaders);

     		  // 
             // Search the fields in the background

             final Runnable runnable = new Runnable()
             {
                 public void run()
                 {
                     StepMeta stepMeta = transMeta.findStep(stepname);
                     if (stepMeta!=null)
                     {
                         try
                         {
                         	RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);
                             // Remember these fields...
                             for (int i=0;i<row.size();i++)
                             {
                                 inputFields.put(row.getValueMeta(i).getName(), Integer.valueOf(i));
                             }
                             setComboBoxes();
                         }
                         catch(KettleException e)
                         {
                         	logError( BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
                         }
                     }
         			 CmisConnector.setLocalNameSpaceFilter(wLocalNameSpaceFilter.getText());
                     CmisConnector.GetDocumentTypeList(wDocumentType,wBaseContentModel);
                 }
             };
             new Thread(runnable).start();

            fdPropertiesComp=new FormData();
      		fdPropertiesComp.left  = new FormAttachment(0, 0);
     		fdPropertiesComp.top   = new FormAttachment(wStepname, margin);
     		fdPropertiesComp.right = new FormAttachment(100, 0);
     		fdPropertiesComp.bottom= new FormAttachment(100, 0);
     		wPropertiesComp.setLayoutData(fdPropertiesComp);
     		
     		wPropertiesComp.layout();
     		wPropertiesTab.setControl(wPropertiesComp);
     		//////// END of Properties Tab
     		
        
        // position tabs on canvas
        fdTabFolder = new FormData();
        fdTabFolder.left  = new FormAttachment(0, 0);
        fdTabFolder.top   = new FormAttachment(wStepname, margin);
        fdTabFolder.right = new FormAttachment(100, 0);
        fdTabFolder.bottom= new FormAttachment(100, -50);
        wTabFolder.setLayoutData(fdTabFolder);
        
        // Set the first tab as default
        wTabFolder.setSelection(0);
        
        // Some buttons
     		wOK=new Button(shell, SWT.PUSH);
     		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
     		wCancel=new Button(shell, SWT.PUSH);
     		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$
     		wAbout=new Button(shell, SWT.PUSH);
     		wAbout.setText(BaseMessages.getString(PKG, "CmisPutDialog.Button.About")); //$NON-NLS-1$

     		setButtonPositions(new Button[] { wOK, wCancel, wAbout }, margin, wTabFolder);
        /////////////////////////////////////////////////////////////
        /// END OF CONTENT TAB
        /////////////////////////////////////////////////////////////
        
		

		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		lsAbout    = new Listener() { public void handleEvent(Event e) { about();     } };
		lsTest    = new Listener() { public void handleEvent(Event e) { test();     } };
		
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener    (SWT.Selection, lsOK    );
		wAbout.addListener(SWT.Selection, lsAbout);
		wTest.addListener(SWT.Selection, lsTest);
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );


		// Set the shell size, based upon previous time...
		setSize();
		
		getData();
		input.setChanged(changed);
	
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}

	protected void GetDocumentProperties(Shell shell) {
        Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(busy);
		CmisConnector.setCmisDialogProperties(wMetaDataList,input.getDocumentType());
        shell.setCursor(null);
	}

	protected void SetVariablePathLogic() {
		if (wHasVariablePath.getSelection()==true) {
			wDynDirStruct.setEnabled(true);
        	wlDynDirStruct.setEnabled(true);
    	} else {
    		wDynDirStruct.setEnabled(false);
        	wlDynDirStruct.setEnabled(false);
    	}
	}

	protected void SetVersionLogic() {
		if (wIsVersioned.getSelection()==true) {
    		wIsMinorVersion.setEnabled(true);
        	wlIsMinorVersion.setEnabled(true);
        	wIsMajorVersion.setEnabled(true);
        	wlIsMajorVersion.setEnabled(true);
        	if ((wIsMinorVersion.getSelection()==false) && (wIsMajorVersion.getSelection()==false)) {
        		wIsMinorVersion.setSelection(true);
        	}
    	} else {
    		wIsMinorVersion.setSelection(false);
    		wIsMinorVersion.setEnabled(false);
        	wlIsMinorVersion.setEnabled(false);
        	wIsMajorVersion.setEnabled(false);
        	wIsMajorVersion.setEnabled(false);
        	wlIsMajorVersion.setEnabled(false);
    	}
	}

    private void getFieldNames(CCombo wComboField)
	{
    	if(!gotPreviousFields)
    	{
    		gotPreviousFields=true;
			try
			{
				String DocumentField=wComboField.getText();
				wComboField.removeAll();
				RowMetaInterface r = transMeta.getPrevStepFields(stepname);
				if (r!=null)
				{
					wComboField.removeAll();
					wComboField.setItems(r.getFieldNames());
				}
				if(DocumentField!=null) wComboField.setText(DocumentField);
			}
			catch(KettleException ke)
			{
				new ErrorDialog(shell, BaseMessages.getString(PKG, "ExecSQLRowDialog.FailedToGetFields.DialogTitle"), 
						BaseMessages.getString(PKG, "ExecSQLRowDialog.FailedToGetFields.DialogMessage"), ke); //$NON-NLS-1$ //$NON-NLS-2$
			}
    	}
	}
	protected void setComboBoxes() {
        // Something was changed in the row.
        //
        final Map<String, Integer> fields = new HashMap<String, Integer>();
        
        // Add the currentMeta fields...
        fields.putAll(inputFields);
        
        Set<String> keySet = fields.keySet();
        List<String> entries = new ArrayList<String>(keySet);

        String fieldNames[] = (String[]) entries.toArray(new String[entries.size()]);

        Const.sortStrings(fieldNames);
        colinf[0].setComboValues(fieldNames);
        dyndirstructcolinf[0].setComboValues(fieldNames);
	}

	protected void setCmisDialogProperties() {
		/*TODO .....shell */
	}

	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
		if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Log.GettingKeyInfo")); //$NON-NLS-1$
		
		wUrl.setText(Const.NVL(input.getUrl(), ""));
	    if(input.getStandard() != null) wStandard.setText(input.getStandard());
	    if(input.getUrl() != null) wUrl.setText(input.getUrl());
	    if(input.getRepository() != null) wRepository.setText(input.getRepository());
	    if(input.getPassword() != null) wPassword.setText(input.getPassword());
	    if(input.getUsername() != null) wUsername.setText(input.getUsername());
	    if(input.getBaseContentModel() != null) wBaseContentModel.setText(input.getBaseContentModel());
	    if(input.getDocumentType() != null) wDocumentType.setText(input.getDocumentType());
	    if(input.getLocalNameSpaceFilter() != null) wLocalNameSpaceFilter.setText(input.getLocalNameSpaceFilter());
	    wIsVersioned.setSelection(input.IsVersioned());
	    wIsMajorVersion.setSelection(input.IsMajorVersion());
	    wIsMinorVersion.setSelection(input.IsMinorVersion());
	    wHasVariablePath.setSelection(input.HasVariablePath());
	    if(input.getToPath() != null) wToPath.setText(input.getToPath());
	    if(input.getDocumentField() != null) wDocumentField.setText(input.getDocumentField());
	    if(input.getFilenamefield() != null) wFileNameField.setText(input.getFilenamefield());
	    if(input.getCmisidfield() != null) wCmisIdField.setText(input.getCmisidfield());
	    
	    if (input.getFolderArgumentField()!=null) {
			for (int i=0;i<input.getFolderArgumentField().length;i++)
			{
				TableItem item = wDynDirStruct.table.getItem(i);
				//TODO fix the error
				if(input.getFolderArgumentField()[i] != null) item.setText(1, Const.NVL(input.getFolderArgumentField()[i], ""));
				if(input.getFolderArgumentFolderType()[i] != null) item.setText(2, Const.NVL(input.getFolderArgumentFolderType()[i], ""));
			}
		}
	    
		if (input.getDocumentPropertyFieldName()!=null) {
			for (int i=0;i<input.getDocumentPropertyFieldName().length;i++)
			{
				TableItem item = wMetaDataList.table.getItem(i);
				if(input.getDocumentPropertyFieldName()[i] != null) item.setText(1, Const.NVL(input.getDocumentPropertyFieldName()[i], ""));
				if(input.getDocumentPropertyName()[i] != null) item.setText(2, Const.NVL(input.getDocumentPropertyName()[i], ""));
				if(input.getDocumentPropertyUpdatability()[i] != null) item.setText(3, Const.NVL(input.getDocumentPropertyUpdatability()[i], ""));
				if(input.getDocumentPropertyCardinality()[i] != null) item.setText(4, Const.NVL(input.getDocumentPropertyCardinality()[i], ""));
				if(input.getDocumentPropertyDisplayName()[i] != null) item.setText(5, Const.NVL(input.getDocumentPropertyDisplayName()[i], ""));
				if(input.getDocumentPropertyDocumentType()[i] != null) item.setText(6, Const.NVL(input.getDocumentPropertyDocumentType()[i], ""));
			}
		}
		SetVersionLogic();
		SetVariablePathLogic();
		wStepname.selectAll();
		
		final Properties props = new Properties();
		props.setProperty("cms.url", transMeta.environmentSubstitute(wUrl.getText()));
		props.setProperty("cms.repoId", transMeta.environmentSubstitute(wRepository.getText()));
		props.setProperty("cms.password", transMeta.environmentSubstitute(wPassword.getText()));
		props.setProperty("cms.username", transMeta.environmentSubstitute(wUsername.getText()));
		CmisConnector = new CmisConnector(props);
		if ((input.getUrl() != null) && (input.getRepository() != null) && (input.getPassword() != null) && (input.getUsername() != null)) {			
			CmisConnector.initCmisSession();
			CmisConnector.setLocalNameSpaceFilter(input.getLocalNameSpaceFilter());
			if (CmisConnector.getSession()==null) {
				disableRepoConfigElements();
			} else {
				enableRepoConfigElements();
			}
		} else {
			disableRepoConfigElements();
		}
	}
	
	private void disableRepoConfigElements() {
		wRepository.setEnabled(false);
		wlRepository.setEnabled(false);
	    wDocTypeGroup.setEnabled(false);
	    wDocumentGroup.setEnabled(false);
	    wVersioningGroup.setEnabled(false);
	}
	
	private void enableRepoConfigElements() {
		wRepository.setEnabled(true);
		wlRepository.setEnabled(true);
	    wDocTypeGroup.setEnabled(true);
	    wDocumentGroup.setEnabled(true);
	    wVersioningGroup.setEnabled(true);
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(changed);
		if (CmisConnector.getSession()!=null) {
			CmisConnector.clearSession();
		}
		dispose();
	}
	
	private void ok()
	{
		if (Const.isEmpty(wStepname.getText())) return;

		stepname = wStepname.getText(); // return value
		input.setStandard(wStandard.getText());
		input.setUrl( wUrl.getText() );
		input.setRepository(wRepository.getText());
		input.setPassword(wPassword.getText());
		input.setUsername(wUsername.getText());
		input.setBaseContentModel(wBaseContentModel.getText());
		input.setDocumentType(wDocumentType.getText());
		input.setLocalNameSpaceFilter(wLocalNameSpaceFilter.getText());
		input.setIsVersioned(wIsVersioned.getSelection());
		input.setIsMajorVersion(wIsMajorVersion.getSelection());
		input.setIsMinorVersion(wIsMinorVersion.getSelection());
		input.setHasVariablePath(wHasVariablePath.getSelection());
		input.setToPath(wToPath.getText());
		input.setDocumentField(wDocumentField.getText());
		input.setFilenamefield(wFileNameField.getText());
		input.setCmisidfield(wCmisIdField.getText());
		
		int nrfolderargs = wDynDirStruct.nrNonEmpty();
		
		input.allocatefolder(nrfolderargs);

		if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Log.FoundFolders",String.valueOf(nrfolderargs))); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i=0;i<nrfolderargs;i++)
		{
			TableItem item = wDynDirStruct.getNonEmpty(i);
			input.setFolderArgumentField(i,item.getText(1));
			input.setFolderArgumentFolderType(i,item.getText(2));
		}
		
		int nrargs = wMetaDataList.nrNonEmpty();
		
		input.allocate(nrargs);
		if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Log.FoundProperties",String.valueOf(nrargs))); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i=0;i<nrargs;i++)
		{
			TableItem item = wMetaDataList.getNonEmpty(i);
			input.setDocumentPropertyFieldName(i,item.getText(1));
			input.setDocumentPropertyName(i,item.getText(2));
			input.setDocumentPropertyUpdatability(i,item.getText(3));
			input.setDocumentPropertyCardinality(i,item.getText(4));
			input.setDocumentPropertyDisplayName(i,item.getText(5));
			input.setDocumentPropertyDocumentType(i,item.getText(6));
		}
		if (CmisConnector.getSession()!=null) {
			CmisConnector.clearSession();	
		}
		dispose();
	}
	
	private void about()
	{
		new CmisPutAboutDialog(CopyOfCmisPutDialog.this.shell).open();
	}

	private void test()
	{
		Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(busy);
		final Properties props = new Properties();
		
		props.setProperty("cms.url", transMeta.environmentSubstitute(wUrl.getText()));
		props.setProperty("cms.password", transMeta.environmentSubstitute(wPassword.getText()));
		props.setProperty("cms.username", transMeta.environmentSubstitute(wUsername.getText()));
		
		if (wRepository.isEnabled()){
			props.setProperty("cms.repoId", transMeta.environmentSubstitute(wRepository.getText()));
			
			if (CmisConnector.getSession()==null){
				CmisConnector.setCmsProperties(props);
				CmisConnector.initCmisSession();
			}
			if (CmisConnector.getSession()==null){
				if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Cmis.ConnectionFailed"));//$NON-NLS-1$ //$NON-NLS-2$
				disableRepoConfigElements();
				new ErrorDialog(shell,BaseMessages.getString(PKG, "CmisPutDialog.Connected.Title.Error"),
					BaseMessages.getString(PKG, "CmisPutDialog.Connected.NOK",transMeta.environmentSubstitute(wUsername.getText())),new Exception(CmisConnector.getMsgError()));
			} else {
				if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Cmis.ConnectionOK"));//$NON-NLS-1$ //$NON-NLS-2$
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION );
				mb.setMessage(BaseMessages.getString(PKG, "CmisPutDialog.Connected.OK",transMeta.environmentSubstitute(wUsername.getText())) +Const.CR);
				mb.setText(BaseMessages.getString(PKG, "CmisPutDialog.Connected.Title.Ok")); 
				mb.open();
			}
		} else {
			CmisConnector.setCmsProperties(props);
			CmisConnector.getRepositories(wRepository);
			
			if (CmisConnector.getRepolist()==null){
				if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Cmis.ConnectionFailed"));//$NON-NLS-1$ //$NON-NLS-2$
				new ErrorDialog(shell,BaseMessages.getString(PKG, "CmisPutDialog.Connected.Title.Error"),
					BaseMessages.getString(PKG, "CmisPutDialog.Connected.NOK",transMeta.environmentSubstitute(wUsername.getText())),new Exception(CmisConnector.getMsgError()));
			} else {
				if(isDebug()) logDebug(BaseMessages.getString(PKG, "CmisPutDialog.Cmis.ConnectionOK"));//$NON-NLS-1$ //$NON-NLS-2$
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION );
				mb.setMessage(BaseMessages.getString(PKG, "CmisPutDialog.Connected.GetRepo",transMeta.environmentSubstitute(wUsername.getText())) +Const.CR);
				mb.setText(BaseMessages.getString(PKG, "CmisPutDialog.Connected.Title.Ok")); 
				mb.open();
				enableRepoConfigElements();
			}
		}
		
		shell.setCursor(null);
		busy.dispose();
	}
}
