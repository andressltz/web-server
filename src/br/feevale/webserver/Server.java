package br.feevale.webserver;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final static String DOCUMENT_ROOT = "/Users/andresschultz/webserver/";
    private final static String HTML_NOT_ALLOWED = "<html><body><p>405 Method Not Allowed</p></body></html>";
    private final static String HTML_NOT_FOUND = "<html><body><p>404 File Not Found</p></body></html>";

    private final static String HEADER_NOT_ALLOWED = "" +
            "HTTP/1.1 405 Method Not Allowed\n" +
            "Content-Type: text/html; charset=utf-8\n" +
            "Connection: close\n" +
            "Content-Length: ";

    private final static String HEADER_NOT_FOUND = "" +
            "HTTP/1.1 404 Not Found\n" +
            "Content-Type: text/html; charset=utf-8\n" +
            "Connection: close\n" +
            "Content-Length: ";

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true) {
                // Request
                Socket socket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String firstLine = bufferedReader.readLine();
                System.out.println("New Request\n" + firstLine + "\n");

                if (firstLine != null && firstLine.startsWith("GET")) {
                    try {
                        File file = getFile(firstLine.substring(4).replace(" HTTP/1.1", ""));
                        FileInputStream in = new FileInputStream(file);
                        byte[] buffer = new byte[256];

                        int read;
                        OutputStream outputStream = socket.getOutputStream();
                        String header = getHeader(file);
                        outputStream.write(header.getBytes());
                        while ((read = in.read(buffer)) > -1) {
                            outputStream.write(buffer, 0, read);
//                            responseOk(socket, buffer);
                        }

                    } catch (FileNotFoundException e) {
                        responseNotFound(socket);
                    }
                } else {
                    responseNotAllowed(socket);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void response(Socket socket, String header, byte[] body) throws IOException {
        // Response
        String output = header + body.length + "\n\n" + new String(body);
        System.out.println("New response\n" + output);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(output.getBytes());
        System.out.println("");
    }

    private static void responseNotAllowed(Socket socket) throws IOException {
        response(socket, HEADER_NOT_ALLOWED, HTML_NOT_ALLOWED.getBytes());
    }

    private static void responseNotFound(Socket socket) throws IOException {
        response(socket, HEADER_NOT_FOUND, HTML_NOT_FOUND.getBytes());
    }

//    private static void responseOk(Socket socket, byte[] body) throws IOException {
//        response(socket, HEADER_OK, body);
//    }

    private static String getHeader(File file) {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\n");
        header.append("Content-Type: ").append(getContentType(file)).append("\n");
        header.append("Connection: close\n");
        header.append("Content-Length: ").append(file.length()).append("\n\n");
        return header.toString();
    }

    private static String getContentType(File file) {
        String filename = file.getName();
        String extension = filename.substring(filename.lastIndexOf("."));
        MimeType mime = MimeType.getByExtension(extension.replace(".", ""));

        if (mime == null) {
            String mimeType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file);
            if (mimeType != null && mimeType.equals("text/html")) {
                return "text/html; charset=utf-8";
            } else {
                return "application/octet-stream";
            }
        }
        return mime.getMimeType();
    }

    private static File getFile(String dest) throws FileNotFoundException {
        File file;
        if (dest.equals("/")) {
            file = new File(DOCUMENT_ROOT + "index.htm");
            if (file.exists()) {
                return file;
            } else {
                file = new File(DOCUMENT_ROOT + "index.html");
                if (file.exists()) {
                    return file;
                } else {
                    throw new FileNotFoundException();
                }
            }
        } else {
            file = new File(DOCUMENT_ROOT + dest);
            if (file.exists()) {
                return file;
            } else {
                throw new FileNotFoundException();
            }
        }
    }
}
