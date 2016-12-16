/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.StringUtil;

public class Formatter {

    private File toFile;
    private String type;
    private boolean toConsole;
    private boolean showSuppressed;
    private final List<Parameter> parameters = new ArrayList<>();

    public void setShowSuppressed(boolean value) {
        this.showSuppressed = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public void setToConsole(boolean toConsole) {
        this.toConsole = toConsole;
    }

    public void addConfiguredParam(Parameter parameter) {
	this.parameters.add(parameter);
    }

    private Writer writer;

    private Renderer renderer;

    public Renderer getRenderer() {
        return renderer;
    }

    public void start(String baseDir) {

        Properties properties = createProperties();
        
        Charset charset;
        {
            String s = (String) properties.get("encoding");
            if (null == s) {
                charset = Charset.forName("UTF-8");
                properties.put("encoding", charset.name());
            }
            else {
                charset = Charset.forName(s);
            }
        }

        try {
            if (toConsole) {
                writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
            }
            if (toFile != null) {
                writer = getToFileWriter(baseDir, toFile, charset);
            }
            renderer = createRenderer();
            renderer.setWriter(writer);
            renderer.start();
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    public void end(Report errorReport) {
        try {
            renderer.renderFileReport(errorReport);
            renderer.end();
            if (toConsole) {
                writer.flush();
            } else {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    public boolean isNoOutputSupplied() {
        return toFile == null && !toConsole;
    }

    @Override
    public String toString() {
        return "file = " + toFile + "; renderer = " + type;
    }

    private static String[] validRendererCodes() {
        return RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet().toArray(new String[RendererFactory.REPORT_FORMAT_TO_RENDERER.size()]);
    }

    private static String unknownRendererMessage(String userSpecifiedType) {
        String[] typeCodes = validRendererCodes();
    	StringBuilder sb = new StringBuilder(100);
        sb.append("Formatter type must be one of: '")
          .append(typeCodes[0]);
        for (int i = 1; i < typeCodes.length; i++) {
            sb.append("', '").append(typeCodes[i]);
        }
        sb.append("', or a class name; you specified: ")
          .append(userSpecifiedType);
        return sb.toString();
    }

    // FIXME - hm, what about this consoleRenderer thing... need a test for this
    Renderer createRenderer() {
        if (StringUtil.isEmpty(type)) {
            throw new BuildException(unknownRendererMessage("<unspecified>"));
        }

        Properties properties = createProperties();
        Renderer renderer = RendererFactory.createRenderer(type, properties);
        renderer.setShowSuppressedViolations(showSuppressed);
        return renderer;
    }

    private Properties createProperties() {
	Properties properties = new Properties();
	for (Parameter parameter : parameters) {
	    properties.put(parameter.getName(), parameter.getValue());
	}
	return properties;
    }

    private static final Writer getToFileWriter(String baseDir, File toFile, Charset charset) throws IOException {
        final File file;
        if (toFile.isAbsolute()) {
            file = toFile;
        }
        else {
            file = new File(baseDir + System.getProperty("file.separator") + toFile.getPath());
        }

        OutputStream output = null;
        Writer writer = null;
        boolean isOnError = true;
        try {
            output = new FileOutputStream(file);
            writer = new OutputStreamWriter(output, charset);
            writer = new BufferedWriter(writer);
            isOnError = false;
        }
        finally {
            if (isOnError) {
                IOUtils.closeQuietly(output, writer);
            }
        }
        return writer;
    }
}
