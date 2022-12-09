package com.gmagnotta.hello_tomcat;

import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotFoundException;

import com.gmagnotta.hello_tomcat.model.LabelledFile;
import com.gmagnotta.hello_tomcat.service.FileLabellerService;

@Named
@RequestScoped
public class FileInfoBean {

    private Logger LOGGER = Logger.getLogger(FileInfoBean.class.getName());

    private LabelledFile labelledFile;

    @Inject
    private FileLabellerService fileLabellerService;

    public LabelledFile getLabelledFile() {
        return labelledFile;
    }

    public void loadFileLabes() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();

        String fileName = (String) requestParams.get("filename");
        LOGGER.info("Requesting " + fileName);

        try {
            labelledFile = fileLabellerService.getFileByName(fileName);
        } catch (NotFoundException ex) {
            labelledFile = null;
        }
    }

}
