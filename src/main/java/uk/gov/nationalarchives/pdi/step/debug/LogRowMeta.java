/**
 * The MIT License
 * Copyright © 2021 The National Archives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.gov.nationalarchives.pdi.step.debug;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;

@Step(id = "LogRowStep", image = "LogRowStep.svg", name = "Debug - Log Row",
        description = "Logs a Row", categoryDescription = "Utility")
public class LogRowMeta extends BaseStepMeta implements StepMetaInterface {

    private static final String ELEM_NAME_ERROR_LOG_CASE = "errorLogCase";
    private static final String ELEM_NAME_NON_ERROR_LOG_DETAIL = "nonErrorLogDetail";

    private ErrorLogCase errorLogCase;
    private NonErrorLogDetail nonErrorLogDetail;

    public LogRowMeta() {
        super();
    }

    @Override
    public void setDefault() {
        this.errorLogCase = ErrorLogCase.ALL;
        this.nonErrorLogDetail = NonErrorLogDetail.NONE;
    }

    @Override
    public String getXML() {
        final StringBuilder builder = new StringBuilder();
        builder
                .append(XMLHandler.addTagValue(ELEM_NAME_ERROR_LOG_CASE, errorLogCase.name()))
                .append(XMLHandler.addTagValue(ELEM_NAME_NON_ERROR_LOG_DETAIL, nonErrorLogDetail.name()));
        return builder.toString();
    }

    @Override
    public void loadXML(final Node stepnode, final List<DatabaseMeta> databases, final IMetaStore metaStore) throws KettleXMLException {
        final String xErrorLogCase = XMLHandler.getTagValue(stepnode, ELEM_NAME_ERROR_LOG_CASE);
        if (xErrorLogCase != null && !xErrorLogCase.isEmpty()) {
            try {
                this.errorLogCase = ErrorLogCase.valueOf(xErrorLogCase);
            } catch (final IllegalArgumentException e) {
                throw new KettleXMLException(e.getMessage(), e);
            }
        }

        final String xNonErrorLogDetail = XMLHandler.getTagValue(stepnode, ELEM_NAME_NON_ERROR_LOG_DETAIL);
        if (xNonErrorLogDetail != null && !xNonErrorLogDetail.isEmpty()) {
            try {
                this.nonErrorLogDetail = NonErrorLogDetail.valueOf(xNonErrorLogDetail);
            } catch (final IllegalArgumentException e) {
                throw new KettleXMLException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void saveRep(final Repository repo, final IMetaStore metaStore, final ObjectId id_transformation, final ObjectId id_step)
            throws KettleException {

        final String rep = getXML();
        repo.saveStepAttribute(id_transformation, id_step, "step-xml", rep);
    }

    @Override
    public void readRep(final Repository repo, final IMetaStore metaStore, final ObjectId id_step, final List<DatabaseMeta> databases) throws KettleException {
        final String rep = repo.getStepAttributeString(id_step, "step-xml");
        if (rep == null || rep.isEmpty()) {
            setDefault();
        }

        final Node stepnode = XMLHandler.loadXMLString(rep);
        loadXML(stepnode, (List<DatabaseMeta>)null, (IMetaStore)null);
    }

    @Override
    public StepInterface getStep(final StepMeta stepMeta, final StepDataInterface stepDataInterface, final int copyNr, final TransMeta transMeta, final Trans trans) {
        return new LogRowStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData() {
        return new LogRowData();
    }

    @Override
    public String getDialogClassName() {
        return "uk.gov.nationalarchives.pdi.step.debug.LogRowDialog";
    }

    // <editor-fold desc="settings getters and setters">

    public ErrorLogCase getErrorLogCase() {
        return errorLogCase;
    }

    public void setErrorLogCase(final ErrorLogCase errorLogCase) {
        this.errorLogCase = errorLogCase;
    }

    public NonErrorLogDetail getNonErrorLogDetail() {
        return nonErrorLogDetail;
    }

    public void setNonErrorLogDetail(final NonErrorLogDetail nonErrorLogDetail) {
        this.nonErrorLogDetail = nonErrorLogDetail;
    }
    // </editor-fold>
}
