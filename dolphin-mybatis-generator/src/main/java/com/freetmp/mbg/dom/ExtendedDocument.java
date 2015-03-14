package com.freetmp.mbg.dom;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Document;

/*
 * Created by LiuPin on 2015/3/2.
 */
public class ExtendedDocument extends Document {

    protected String fileComments;

    /*
     * @param publicId document identify
     * @param systemId document identify
     */
    public ExtendedDocument(String publicId, String systemId) {
        super(publicId, systemId);
    }

    public ExtendedDocument() {
        super();
    }

    public ExtendedDocument(Document document){
        super(document.getPublicId(),document.getSystemId());
        setRootElement(document.getRootElement());
    }

    public String getFileComments() {
        return fileComments;
    }

    public void setFileComments(String fileComments) {
        this.fileComments = fileComments;
    }

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); //$NON-NLS-1$

        if (getPublicId() != null && getSystemId() != null) {
            OutputUtilities.newLine(sb);
            sb.append("<!DOCTYPE "); //$NON-NLS-1$
            sb.append(getRootElement().getName());
            sb.append(" PUBLIC \""); //$NON-NLS-1$
            sb.append(getPublicId());
            sb.append("\" \""); //$NON-NLS-1$
            sb.append(getSystemId());
            sb.append("\" >"); //$NON-NLS-1$
        }

        // add file comments to the generated string
        if(StringUtils.isNotEmpty(fileComments)) {
            OutputUtilities.newLine(sb);
            sb.append(fileComments);
        }

        OutputUtilities.newLine(sb);
        sb.append(getRootElement().getFormattedContent(0));

        return sb.toString();
    }
}
