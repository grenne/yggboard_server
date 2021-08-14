package com.yggboard;


//
// @Lock(LockType.READ)
//@GetMapping("/upload")

public class Rest_UploadFiles {
//
//	MongoClient mongo = new MongoClient();
//	/**
//	 * Procura a imagem pelo seu nome e devolve como resposta.
//	 * @author magician
//	 *
//     * Devolve a imagem com o mime type do ficheiro ou 404 caso
//     * o ficheiro n�o seja encontrada.
//     * @param image nome da imagem a procurar
//     * @return imagem com o mime type da imagem fonte.
//     */
//	
//	@GetMapping("/images")
//    @Produces("image/*")
//    public Response getImage(@RequestParam("image") String image){
//
//		String tmp = "c:/images/yggboard/";
//		Commons_DB commons_db = new Commons_DB();
//		BasicDBObject cursor = commons_db.getCollection("fotosYggboard", "objetivos", "documento.setupKey", mongo, false);
//		if (cursor != null){
//			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
//			tmp = obj.getString("setupValue");
//		};
//
//		File target = new File(tmp + image);
//        if(!target.exists()){
//            throw new WebApplicationException(404);
//        }
//        String mt = new MimetypesFileTypeMap().getContentType(target);
//        mongo.close();
//        return Response.ok(target, mt).build();
//    }
//
//	@POST
//	@GetMapping("/files")
//	@Consumes("multipart/form-data")
//	public Response uploadFile(MultipartFormDataInput input, @RequestParam("prefix") String prefix) {
//		String tmp = "c:/images/yggboard/";
//		Commons_DB commons_db = new Commons_DB();
//		BasicDBObject cursor = commons_db.getCollection("fotosYggboard", "objetivos", "documento.setupKey", mongo, false);
//		if (cursor != null){
//			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
//			tmp = obj.getString("setupValue");
//		};
//		String fileName = "";
//		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
//		List<InputPart> inputParts = uploadForm.get("uploadedFile");
//		for (InputPart inputPart : inputParts) {
//
//			try {
//
//				MultivaluedMap<String, String> header = inputPart.getHeaders();
//				fileName = prefix + "_" + getFileName(header);
//
//				//convert the 	uploaded file to inputstream
//				InputStream inputStream = inputPart.getBody(InputStream.class,null);
//
//				byte [] bytes = IOUtils.toByteArray(inputStream);
//
//				//constructs upload file path
//				fileName = tmp + fileName;
//
//				writeFile(bytes,fileName);
//
//				System.out.println("Done");
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		};
//
//		mongo.close();
//		return Response.status(200)
//				.entity("uploadFile is called, Uploaded file name : " + fileName).build();
//
//	}
//
//	/**
//	 * header sample
//	 * {
//	 * 		Content-Type=[image/png],
//	 * 		Content-Disposition=[form-data; name="file"; filename="filename.extension"]
//	 * }
//	 **/
//	//get uploaded filename, is there a easy way in RESTEasy?
//	private String getFileName(MultivaluedMap<String, String> header) {
//
//		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
//
//		for (String filename : contentDisposition) {
//			if ((filename.trim().startsWith("filename"))) {
//
//				String[] name = filename.split("=");
//
//				String finalFileName = name[1].trim().replaceAll("\"", "");
//				return finalFileName;
//			}
//		}
//		return "unknown";
//	}
//
//	//save to somewhere
//	private void writeFile(byte[] content, String filename) throws IOException {
//
//		File file = new File(filename);
//
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//
//		FileOutputStream fop = new FileOutputStream(file);
//
//		fop.write(content);
//		fop.flush();
//		fop.close();
//
//	};
//
}
