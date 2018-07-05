//package single.practice.utils;
//
//import org.apache.commons.io.FileUtils;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.nio.file.StandardOpenOption;
//import java.nio.file.attribute.*;
//import java.util.ArrayList;
//import java.util.EnumSet;
//import java.util.List;
//import java.util.Set;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//public abstract class ZipUtils {
//    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
//    public static void zipFile(String destZip,String src)
//    {
//
//        LOGGER.info("--------------------------压缩中----------------------------");
//        ZipOutputStream out =null;
//        //OutputStream ou =null;
//        try {
//            //ou = new FileOutputStream(destZip);
//            LOGGER.info("-------------------------fileoutputstream-----------------------------"+destZip);
//            //创建zip输出流
//            //String destZipPath = destZip.substring(0, destZip.indexOf("."));
//            //logger.info(Logger.EVENT_SUCCESS,"-------------------------path-----------------------------"+destZipPath);
//            OutputStream ou = getSafeOutputStream(destZip,true);
//            LOGGER.info("-------------------------getSafeOutputStream,checkoutZip-----------------------------");
//            out = new ZipOutputStream(ou);
//            //out = new ZipOutputStream(new FileOutputStream(destZip));
//            LOGGER.info("-------------------------zipoutpustream----------------------");
//            File sourceFile = FileUtils.getFile(src);
//            //logger.info(Normalizer.normalize(sourceFile.getName(), Normalizer.Form.NFC));
//            if(null!=sourceFile) {
//                //调用函数
//                compress(out, sourceFile, sourceFile.getName());
//            }
//            LOGGER.info("-----------------------------压缩完成-----------------------------");
//        } catch (IOException e) {
//            LOGGER.error("-----------------------Failed zipoutputstream--------------------------",e);
//        } finally {
//            releaseStream(out);
//            //FileUtil.releaseStream(ou);
//        }
//
//    }
//    private static void compress(ZipOutputStream out, File sourceFile, String base) throws IOException {
//        //如果路径为目录（文件夹）
//        if(sourceFile.isDirectory())
//        {
//            //取出文件夹中的文件（或子文件夹）
//            File[] list = sourceFile.listFiles();
//            if(list==null ||list.length==0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
//            {
//                out.putNextEntry(  new ZipEntry(base+"/") );
//            }
//            else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
//            {
//                for(int i=0;i<list.length;i++)
//                {
//                    compress(out,list[i],base+"/"+list[i].getName());
//                }
//            }
//        }
//        else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
//        {
//            out.putNextEntry( new ZipEntry(base) );
//
//            FileInputStream fos =null;
//            BufferedInputStream bis =null;
//            try {
//
//                fos = new FileInputStream(FileUtils.getFile(sourceFile));
//                bis = new BufferedInputStream(fos);
//                LOGGER.info("----------------------file:" + base);
//
//                int tag = 0;
//                while ((tag = bis.read()) != -1) {
//                    out.write(tag);
//                }
//            } catch (IOException e) {
//
//                LOGGER.info("------------------------Failed stream");
//            } finally {
//                releaseStream(bis);
//                releaseStream(fos);
//            }
//
//        }
//    }
//
//    private static OutputStream getSafeOutputStream(String filePath, boolean isGroupReadShare)
//            throws IOException {
//        File file = FileUtils.getFile(filePath);
//        if(null!=file){
//            Path path = file.toPath();
//            FileAttribute<Set<PosixFilePermission>> attr = getDefaultFileAttribute(file,isGroupReadShare);
//            EnumSet<StandardOpenOption> localEnumSet = EnumSet.of(StandardOpenOption.CREATE,
//                    StandardOpenOption.WRITE);
//            java.nio.file.Files.newByteChannel(path, localEnumSet, attr).close();
//            OutputStream out = java.nio.file.Files.newOutputStream(path);
//            return out;
//        } else {
//            throw new ServiceException("------------------file is null-------------");
//        }
//    }
//    private static FileAttribute<Set<PosixFilePermission>> getDefaultFileAttribute(
//            File file, boolean isReadShare) {
//        Path path = file.toPath();
//        // File permissions should be such that only user may read/write file
//        FileAttribute<?> fa = null;
//        boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains(
//                "posix");
//        if ( isPosix ) { //linux 平台权限控制
//            String permissons = isReadShare ? "rw-r-----" : "rw-------";
//            Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissons);
//            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
//            fa = attr;
//        } else { // Windows 平台权限控制
//            // for not posix must support ACL, or failed.
//            String userName = System.getProperty("user.name");
//            UserPrincipal user = null;
//            try {
//                user = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(
//                        userName);
//            } catch (IOException e) {
//                LOGGER.error("GET Filesystem FAIL");
//            }
//            AclEntryPermission[] permList = new AclEntryPermission[] {
//                    AclEntryPermission.READ_DATA,
//                    AclEntryPermission.READ_ATTRIBUTES,
//                    AclEntryPermission.READ_NAMED_ATTRS,
//                    AclEntryPermission.READ_ACL, AclEntryPermission.WRITE_DATA,
//                    AclEntryPermission.DELETE, AclEntryPermission.APPEND_DATA,
//                    AclEntryPermission.WRITE_ATTRIBUTES,
//                    AclEntryPermission.WRITE_NAMED_ATTRS,
//                    AclEntryPermission.WRITE_ACL,
//                    AclEntryPermission.SYNCHRONIZE};
//            Set<AclEntryPermission> perms = EnumSet.noneOf(AclEntryPermission.class);
//            for ( AclEntryPermission perm : permList )
//                perms.add(perm);
//
//            final AclEntry entry = AclEntry.newBuilder().setType(
//                    AclEntryType.ALLOW).setPrincipal(user).setPermissions(perms).setFlags(
//                    new AclEntryFlag[] {AclEntryFlag.FILE_INHERIT,
//                            AclEntryFlag.DIRECTORY_INHERIT}).build();
//
//            FileAttribute<List<AclEntry>> aclattrs = null;
//            aclattrs = new FileAttribute<List<AclEntry>>() {
//                public String name() {
//                    return "acl:acl";
//                } /* Windows ACL */
//
//                public List<AclEntry> value() {
//                    ArrayList<AclEntry> l = new ArrayList<AclEntry>();
//                    l.add(entry);
//                    return l;
//                }
//            };
//            fa = aclattrs;
//        }
//
//        return (FileAttribute<Set<PosixFilePermission>>)fa;
//    }
//
//    public static void releaseStream(FileInputStream in){
//        try{
//            if(in != null ){
//                in.close();
//            }
//        }catch (IOException e){
//            LOGGER.error("close stream failed ");
//        }
//    }
//
//    public static void releaseStream(InputStream in){
//        try{
//            if(in != null ){
//                in.close();
//            }
//        }catch (IOException e){
//            LOGGER.error("close stream failed ");
//        }
//    }
//
//    public static void releaseStream(OutputStream out){
//        try{
//            if(out != null ){
//                out.close();
//            }
//        }catch (IOException e){
//            LOGGER.error("close stream failed ");
//        }
//    }
//}
