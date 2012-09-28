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

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * @author Gunter Rombauts
 * @since 26-sep-2011
 */



public class CmisPutData extends BaseStepData implements StepDataInterface
{
	public RowMetaInterface outputRowMeta;
	public int nrInfields;
	public RowMetaInterface previousRowMeta;
	
	/*id of the field containing the cmsfilename */
	public int filenamefieldid;

	/*id of the field containing the Document name */
	public int  documentfieldid;
	
	/*array of indexes of folder arguments */
	public int[]  folderArgumentIndexes;
	/*array of indexes of folder arguments */
	public int[]  propertiesArgumentIndexes;
	
	/**
	 * 
	 */
	public CmisPutData()
	{
		super();
	}

}
