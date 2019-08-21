/*
 * Sample module in the public domain.  Feel free to use this as a template
 * for your modules.
 * 
 *  Contact: Brian Carrier [carrier <at> sleuthkit [dot] org]
 *
 *  This is free and unencumbered software released into the public domain.
 *  
 *  Anyone is free to copy, modify, publish, use, compile, sell, or
 *  distribute this software, either in source code form or as a compiled
 *  binary, for any purpose, commercial or non-commercial, and by any
 *  means.
 *  
 *  In jurisdictions that recognize copyright laws, the author or authors
 *  of this software dedicate any and all copyright interest in the
 *  software to the public domain. We make this dedication for the benefit
 *  of the public at large and to the detriment of our heirs and
 *  successors. We intend this dedication to be an overt act of
 *  relinquishment in perpetuity of all present and future rights to this
 *  software under copyright law.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE. 
 */
package org.qrcode.autopsy;

import com.google.zxing.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.autopsy.ingest.ModuleDataEvent;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardArtifact.ARTIFACT_TYPE;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;

/**
 * Sample file ingest module that doesn't do much. Demonstrates per ingest job
 * module settings, use of a subset of the available ingest services and
 * thread-safe sharing of per ingest job data.
 */
/**
 *
 * @author Harley
 */
class SampleFileIngestModule implements FileIngestModule {

    private static final HashMap<Long, Long> artifactCountsForIngestJobs = new HashMap<>();
    private static BlackboardAttribute.ATTRIBUTE_TYPE attrType = BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD;
    private static  boolean skipKnownFiles;
    private IngestJobContext context = null;
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();
    private static Report report;
    
    SampleFileIngestModule(SampleModuleIngestJobSettings settings) {
        this.skipKnownFiles = settings.skipKnownFiles();
    }
    
    @Override
    public synchronized void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;
        refCounter.incrementAndGet(context.getJobId());
        String path = "D:/"+ String.valueOf(context.getJobId());
        report = new Report();
        report.startup(path);
    }

    @Override
    public  IngestModule.ProcessResult process(AbstractFile file) {

        // Skip anything other than actual file system files.
        if ((file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS)
                || (file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNUSED_BLOCKS)
                || (file.isFile() == false)) {
            return IngestModule.ProcessResult.OK;
        }

        // Skip NSRL / known files.
        if (skipKnownFiles && file.getKnown() == TskData.FileKnown.KNOWN) {
            return IngestModule.ProcessResult.OK;
        }

        // Deliver the file to QR code module 
        try {
            QRCodeScanner scan = new QRCodeScanner(file.getLocalAbsPath());
            List<String> decodedData = scan.decode();
            //String version=scan.getVersion(file.getLocalAbsPath());
            // Make an attribute using the ID for the attribute attrType that 
            // was previously created.
            //BlackboardAttribute attr = new BlackboardAttribute(attrType, SampleIngestModuleFactory.getModuleName(), count);
            
            BlackboardAttribute attr = new BlackboardAttribute(attrType, SampleIngestModuleFactory.getModuleName(), decodedData.get(0));

            // Add the to the general info artifact for the file. In a
            // real module, you would likely have more complex data types 
            // and be making more specific artifacts.
            BlackboardArtifact art = file.getGenInfoArtifact();
            art.addAttribute(attr);

            // This method is thread-safe with per ingest job reference counted
            // management of shared data.
            addToBlackboardPostCount(context.getJobId(), 1L);

            // Fire an event to notify any listeners for blackboard postings.
            ModuleDataEvent event = new ModuleDataEvent(SampleIngestModuleFactory.getModuleName(), ARTIFACT_TYPE.TSK_GEN_INFO);
            IngestServices.getInstance().fireModuleDataEvent(event);
            
            if(!decodedData.get(0).equals("Not an Image")&&!decodedData.get(0).equals("Not a QR Code")){
                String QRType = report.parseQR(decodedData.get(0));
                report.addFileAttributes(file.getName(),file.getLocalPath(), decodedData.get(0), decodedData.get(3), 
                        QRType, file.getNameExtension(), decodedData.get(2),decodedData.get(1));//添加info到list
            }

            return IngestModule.ProcessResult.OK;
        } catch (TskCoreException | NullPointerException | NotFoundException ex) {
            IngestServices ingestServices = IngestServices.getInstance();
            Logger logger = ingestServices.getLogger(SampleIngestModuleFactory.getModuleName());
            logger.log(Level.SEVERE, "Error processing file (id = " + file.getId() + ")", ex);
            return IngestModule.ProcessResult.ERROR;
        }
    
    }
  

    @Override
    public synchronized void shutDown() {
        // This method is thread-safe with per ingest job reference counted
        // management of shared data.
        report.generateReport();
        reportBlackboardPostCount(context.getJobId());
    }

    synchronized static  void addToBlackboardPostCount(long ingestJobId, long countToAdd) {
        Long fileCount = artifactCountsForIngestJobs.get(ingestJobId);

        // Ensures that this job has an entry
        if (fileCount == null) {
            fileCount = 0L;
            artifactCountsForIngestJobs.put(ingestJobId, fileCount);
        }

        fileCount += countToAdd;
        artifactCountsForIngestJobs.put(ingestJobId, fileCount);
    }

    synchronized static  void reportBlackboardPostCount(long ingestJobId) {
        Long refCount = refCounter.decrementAndGet(ingestJobId);
        if (refCount == 0) {
            Long filesCount = artifactCountsForIngestJobs.remove(ingestJobId);
            String msgText = String.format("Posted %d times to the blackboard", filesCount);
            IngestMessage message = IngestMessage.createMessage(
                    IngestMessage.MessageType.INFO,
                    SampleIngestModuleFactory.getModuleName(),
                    msgText);
            IngestServices.getInstance().postMessage(message);
        }
    }
}
