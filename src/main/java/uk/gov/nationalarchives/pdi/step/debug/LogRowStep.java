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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

public class LogRowStep extends BaseStep implements StepInterface {

    private static final String EOL = System.getProperty("line.separator");

    public LogRowStep(final StepMeta stepMeta, final StepDataInterface stepDataInterface, final int copyNr,
                         final TransMeta transMeta, final Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public boolean processRow(final StepMetaInterface smi, final StepDataInterface sdi) throws KettleException {
        final Object[] row = getRow();
        if (row == null) {
            setOutputDone();
            return false;
        }

        final LogRowMeta meta = (LogRowMeta)smi;

        final RowMetaInterface inputRowMeta = getInputRowMeta();
        final RowMetaInterface outputRowMeta = inputRowMeta.clone();
        smi.getFields(outputRowMeta, getStepname(), null, null, this, repository, metaStore);

        final ErrorLogCase errorLogCase = meta.getErrorLogCase();

        if (ErrorLogCase.ALL == errorLogCase ||
                (ErrorLogCase.FIRST == errorLogCase && first) ||
                (ErrorLogCase.ROW_UNDER_RUN == errorLogCase && inputRowMeta.size() > row.length)) {

            logError(detailedMessage(inputRowMeta, row));

        } else {

            final NonErrorLogDetail nonErrorLogDetail = meta.getNonErrorLogDetail();

            if (NonErrorLogDetail.ALL_BASIC == nonErrorLogDetail ||
                    (NonErrorLogDetail.FIRST_BASIC == nonErrorLogDetail && first)) {

                logBasic(basicMessage(inputRowMeta, row));

            } else if (NonErrorLogDetail.ALL_DETAILED == nonErrorLogDetail ||
                    (NonErrorLogDetail.FIRST_DETAILED == nonErrorLogDetail && first)) {

                logBasic(detailedMessage(inputRowMeta, row));
            }
        }

        putRow(outputRowMeta, row);

        if (first) {
            first = false;
        }

        return true;
    }

    private String basicMessage(final RowMetaInterface inputRowMeta, final Object[] row) {
        return messageHeader(new StringBuilder(), inputRowMeta, row).toString();
    }

    private StringBuilder messageHeader(final StringBuilder buf, final RowMetaInterface inputRowMeta, final Object[] row) {
        return buf.append("inputRowMeta.size()=").append(inputRowMeta.size()).append(inputRowMeta.size() > row.length ? " > " : ", ").append("r.length=").append(row.length);
    }

    private String detailedMessage(final RowMetaInterface inputRowMeta, final Object[] row) {
        final StringBuilder buf = messageHeader(new StringBuilder(), inputRowMeta, row);
        buf.append(EOL);
        buf.append("META={");
        for (int i = 0; i < inputRowMeta.size(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            final ValueMetaInterface valueMeta = inputRowMeta.getValueMeta(i);
            buf.append(valueMeta.getName());
        }
        buf.append("}");
        buf.append(EOL);
        buf.append("ROW={");
        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            final Object field = row[i];
            buf.append(field == null ? "null" : field.toString());
        }
        buf.append("}");

        return buf.toString();
    }
}
