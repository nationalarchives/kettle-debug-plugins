/*
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class LogRowDialog extends BaseStepDialog implements StepDialogInterface {

    private static Class<?> PKG = LogRowMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

    private static final int MARGIN_SIZE = 15;
    private static final int LABEL_SPACING = 5;
    private static final int ELEMENT_SPACING = 10;

    private static final int LARGE_FIELD = 350;
    private static final int MEDIUM_FIELD = 250;

    private final LogRowMeta meta;

    private ScrolledComposite scrolledComposite;
    private Composite contentComposite;
    private Label wStepNameLabel;
    private Text wStepNameField;
    private Label wErrorLogCaseLabel;
    private Combo wErrorLogCaseField;
    private Label wNonErrorLogDetailLabel;
    private Combo wNonErrorLogDetailField;
    private Button wCancel;
    private Button wOK;
    private ModifyListener lsChanges;

    public LogRowDialog(final Shell parent, final Object in, final TransMeta transMeta, final String stepname) {
        super(parent, (BaseStepMeta) in, transMeta, stepname);
        meta = (LogRowMeta) in;
    }

    @Override
    public String open() {
        //Set up window
        final Shell parent = getParent();
        final Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        shell.setMinimumSize(450, 335);
        props.setLook(shell);
        setShellImage(shell, meta);

        lsChanges = new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent modifyEvent) {
                meta.setChanged();
            }
        };
        changed = meta.hasChanged();

        //15 pixel margins
        final FormLayout formLayout = new FormLayout();
        formLayout.marginLeft = MARGIN_SIZE;
        formLayout.marginHeight = MARGIN_SIZE;
        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "LogRowDialog.Shell.Title"));

        //Build a scrolling composite and a composite for holding all content
        scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
        contentComposite = new Composite(scrolledComposite, SWT.NONE);
        final FormLayout contentLayout = new FormLayout();
        contentLayout.marginRight = MARGIN_SIZE;
        contentComposite.setLayout(contentLayout);
        final FormData compositeLayoutData = new FormDataBuilder().fullSize()
                .result();
        contentComposite.setLayoutData(compositeLayoutData);
        props.setLook(contentComposite);

        //Step name label and text field
        wStepNameLabel = new Label(contentComposite, SWT.RIGHT);
        wStepNameLabel.setText(BaseMessages.getString(PKG, "LogRowDialog.Stepname.Label"));
        props.setLook(wStepNameLabel);
        final FormData fdStepNameLabel = new FormDataBuilder().left()
                .top()
                .result();
        wStepNameLabel.setLayoutData(fdStepNameLabel);

        wStepNameField = new Text(contentComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepNameField.setText(stepname);
        props.setLook(wStepNameField);
        wStepNameField.addModifyListener(lsChanges);
        final FormData fdStepName = new FormDataBuilder().left()
                .top(wStepNameLabel, LABEL_SPACING)
                .width(MEDIUM_FIELD)
                .result();
        wStepNameField.setLayoutData(fdStepName);

        //Job icon, centered vertically between the top of the label and the bottom of the field.
        final Label wicon = new Label(contentComposite, SWT.CENTER);
        wicon.setImage(getImage());
        final FormData fdIcon = new FormDataBuilder().right()
                .top(0, 4)
                .bottom(new FormAttachment(wStepNameField, 0, SWT.BOTTOM))
                .result();
        wicon.setLayoutData(fdIcon);
        props.setLook(wicon);

        //Spacer between entry info and content
        final Label topSpacer = new Label(contentComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
        final FormData fdSpacer = new FormDataBuilder().fullWidth()
                .top(wStepNameField, MARGIN_SIZE)
                .result();
        topSpacer.setLayoutData(fdSpacer);

        //Groups for first type of content
        final Group group = new Group(contentComposite, SWT.SHADOW_ETCHED_IN);
        group.setText(BaseMessages.getString(PKG, "LogRowDialog.GroupText.Settings"));
        final FormLayout groupLayout = new FormLayout();
        groupLayout.marginWidth = MARGIN_SIZE;
        groupLayout.marginHeight = MARGIN_SIZE;
        group.setLayout(groupLayout);
        final FormData groupLayoutData = new FormDataBuilder().fullWidth()
                .top(topSpacer, MARGIN_SIZE)
                .result();
        group.setLayoutData(groupLayoutData);
        props.setLook(group);

        // error log case label/checkbox
        wErrorLogCaseLabel = new Label(group, SWT.LEFT);
        props.setLook(wErrorLogCaseLabel);
        wErrorLogCaseLabel.setText(BaseMessages.getString(PKG, "LogRowDialog.ComboErrorLogCase"));
        final FormData fdErrorLogCaseLabel = new FormDataBuilder().left()
                .top()
                .result();
        wErrorLogCaseLabel.setLayoutData(fdErrorLogCaseLabel);

        wErrorLogCaseField = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wErrorLogCaseField.setItems(ErrorLogCase.names());
        props.setLook(wErrorLogCaseField);
        wErrorLogCaseField.addModifyListener(lsChanges);
        wErrorLogCaseField.setBackground(display.getSystemColor(SWT.COLOR_TRANSPARENT));
        final FormData fdErrorLogCaseField = new FormDataBuilder().left(wErrorLogCaseLabel, LABEL_SPACING)
                .top()
                .result();
        wErrorLogCaseField.setLayoutData(fdErrorLogCaseField);

        // non-error log detail label/checkbox
        wNonErrorLogDetailLabel = new Label(group, SWT.LEFT);
        props.setLook(wNonErrorLogDetailLabel);
        wNonErrorLogDetailLabel.setText(BaseMessages.getString(PKG, "LogRowDialog.ComboNonErrorLogDetail"));
        final FormData fdNonErrorLogDetailLabel = new FormDataBuilder().left()
                .top(wErrorLogCaseLabel)
                .result();
        wNonErrorLogDetailLabel.setLayoutData(fdNonErrorLogDetailLabel);

        wNonErrorLogDetailField = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wNonErrorLogDetailField.setItems(NonErrorLogDetail.names());
        props.setLook(wNonErrorLogDetailField);
        wNonErrorLogDetailField.addModifyListener(lsChanges);
        wNonErrorLogDetailField.setBackground(display.getSystemColor(SWT.COLOR_TRANSPARENT));
        final FormData fdNonErrorLogDetailField = new FormDataBuilder().left(wNonErrorLogDetailLabel, LABEL_SPACING)
                .top(wErrorLogCaseLabel)
                .result();
        wNonErrorLogDetailField.setLayoutData(fdNonErrorLogDetailField);

        //Cancel and OK buttons for the bottom of the window.
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
        final FormData fdCancel = new FormDataBuilder().right(100, -MARGIN_SIZE)
                .bottom()
                .result();
        wCancel.setLayoutData(fdCancel);

        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        final FormData fdOk = new FormDataBuilder().right(wCancel, -LABEL_SPACING)
                .bottom()
                .result();
        wOK.setLayoutData(fdOk);

        //Space between bottom buttons and the table, final layout for table
        final Label bottomSpacer = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
        final FormData fdhSpacer = new FormDataBuilder().left()
                .right(100, -MARGIN_SIZE)
                .bottom(wCancel, -MARGIN_SIZE)
                .result();
        bottomSpacer.setLayoutData(fdhSpacer);

        //Add everything to the scrolling composite
        scrolledComposite.setContent(contentComposite);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        scrolledComposite.setLayout(new FormLayout());
        final FormData fdScrolledComposite = new FormDataBuilder().fullWidth()
                .top()
                .bottom(bottomSpacer, -MARGIN_SIZE * 4)
                .result();
        scrolledComposite.setLayoutData(fdScrolledComposite);
        props.setLook(scrolledComposite);

        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(final SelectionEvent e) {
                ok();
            }
        };
        lsCancel = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                cancel();
            }
        };
        lsOK = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                ok();
            }
        };

        wStepNameField.addSelectionListener(lsDef);
        wOK.addListener(SWT.Selection, lsOK);
        wCancel.addListener(SWT.Selection, lsCancel);

        shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        //Show shell
        setSize();
        getData(meta);
        meta.setChanged(changed);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return stepname;
    }

    private void getData(final LogRowMeta meta) {
        final ErrorLogCase errorLogCase = meta.getErrorLogCase();
        wErrorLogCaseField.setText(errorLogCase.name());

        final NonErrorLogDetail nonErrorLogDetail = meta.getNonErrorLogDetail();
        wNonErrorLogDetailField.setText(nonErrorLogDetail.name());
    }

    private void saveData() {
        final String xErrorLogCase = wErrorLogCaseField.getText();
        if (xErrorLogCase != null && !xErrorLogCase.isEmpty()) {
            meta.setErrorLogCase(ErrorLogCase.valueOf(xErrorLogCase));
        }

        final String xNonErrorLogDetail = wNonErrorLogDetailField.getText();
        if (xNonErrorLogDetail != null && !xNonErrorLogDetail.isEmpty()) {
            meta.setNonErrorLogDetail(NonErrorLogDetail.valueOf(xNonErrorLogDetail));
        }
    }

    private Image getImage() {
        final PluginInterface plugin =
                PluginRegistry.getInstance().getPlugin(StepPluginType.class, stepMeta.getStepMetaInterface());
        final String id = plugin.getIds()[0];
        if (id != null) {
            return GUIResource.getInstance().getImagesSteps().get(id).getAsBitmapForSize(shell.getDisplay(),
                    ConstUI.ICON_SIZE, ConstUI.ICON_SIZE);
        }
        return null;
    }

    private void cancel() {
        dispose();
    }

    private void ok() {
        // SAVE DATA
        saveData();

        // NOTIFY CHANGE
        meta.setChanged(true);

        stepname = wStepNameField.getText();
        dispose();
    }
}
