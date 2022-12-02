package com.gmagnotta.hello_tomcat;

import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.gmagnotta.hello_tomcat.model.LabelledFile;
import com.gmagnotta.hello_tomcat.service.FileLabellerService;

@Named
@RequestScoped
public class FileInfoBean {

    private Logger LOGGER = Logger.getLogger(FileInfoBean.class.getName());

    @Inject
    private FileLabellerService fileLabellerService;

    public LabelledFile getLabelledFile() {

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();

        String fileName = (String) requestParams.get("filename");
        LOGGER.info("Requested filename " + fileName);

        return fileLabellerService.getFileByName(fileName);

    }

}
