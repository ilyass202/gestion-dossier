package app.fichier.Exception;


public class DocumentExceptionStorage extends RuntimeException{

    public DocumentExceptionStorage(String message){
        super(message);
    }
    public DocumentExceptionStorage(String message, Throwable cause){
        super(message, cause);
    
}
}
