package com.cisco.wccai.grpc.model;


import com.cisco.wcc.ccai.v1.CcaiApi;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private CcaiApi.StreamingAnalyzeContentResponse callStartResponse;
    private CcaiApi.StreamingAnalyzeContentResponse startOfInputResponse;
    private CcaiApi.StreamingAnalyzeContentResponse partialRecognitionResponse;
    private CcaiApi.StreamingAnalyzeContentResponse endOfInputResponse;
    private CcaiApi.StreamingAnalyzeContentResponse finalRecognitionResponse;
    private CcaiApi.StreamingAnalyzeContentResponse finalVAResponse;
    private CcaiApi.StreamingAnalyzeContentResponse finalDTMFResponse;
    private CcaiApi.StreamingAnalyzeContentResponse callEndResponse;
    private CcaiApi.StreamingAnalyzeContentResponse aaResponse;

    // Manual getter methods (Lombok backup)
    public CcaiApi.StreamingAnalyzeContentResponse getCallStartResponse() {
        return callStartResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getStartOfInputResponse() {
        return startOfInputResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getPartialRecognitionResponse() {
        return partialRecognitionResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getEndOfInputResponse() {
        return endOfInputResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getFinalRecognitionResponse() {
        return finalRecognitionResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getFinalVAResponse() {
        return finalVAResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getFinalDTMFResponse() {
        return finalDTMFResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getCallEndResponse() {
        return callEndResponse;
    }

    public CcaiApi.StreamingAnalyzeContentResponse getAaResponse() {
        return aaResponse;
    }

    // Manual setter methods (Lombok backup)
    public void setCallStartResponse(CcaiApi.StreamingAnalyzeContentResponse callStartResponse) {
        this.callStartResponse = callStartResponse;
    }

    public void setStartOfInputResponse(CcaiApi.StreamingAnalyzeContentResponse startOfInputResponse) {
        this.startOfInputResponse = startOfInputResponse;
    }

    public void setPartialRecognitionResponse(CcaiApi.StreamingAnalyzeContentResponse partialRecognitionResponse) {
        this.partialRecognitionResponse = partialRecognitionResponse;
    }

    public void setEndOfInputResponse(CcaiApi.StreamingAnalyzeContentResponse endOfInputResponse) {
        this.endOfInputResponse = endOfInputResponse;
    }

    public void setFinalRecognitionResponse(CcaiApi.StreamingAnalyzeContentResponse finalRecognitionResponse) {
        this.finalRecognitionResponse = finalRecognitionResponse;
    }

    public void setFinalVAResponse(CcaiApi.StreamingAnalyzeContentResponse finalVAResponse) {
        this.finalVAResponse = finalVAResponse;
    }

    public void setFinalDTMFResponse(CcaiApi.StreamingAnalyzeContentResponse finalDTMFResponse) {
        this.finalDTMFResponse = finalDTMFResponse;
    }

    public void setCallEndResponse(CcaiApi.StreamingAnalyzeContentResponse callEndResponse) {
        this.callEndResponse = callEndResponse;
    }

    public void setAaResponse(CcaiApi.StreamingAnalyzeContentResponse aaResponse) {
        this.aaResponse = aaResponse;
    }

    // Manual builder method (Lombok backup)
    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    // Manual Builder class (Lombok backup)
    public static class ResponseBuilder {
        private CcaiApi.StreamingAnalyzeContentResponse callStartResponse;
        private CcaiApi.StreamingAnalyzeContentResponse startOfInputResponse;
        private CcaiApi.StreamingAnalyzeContentResponse partialRecognitionResponse;
        private CcaiApi.StreamingAnalyzeContentResponse endOfInputResponse;
        private CcaiApi.StreamingAnalyzeContentResponse finalRecognitionResponse;
        private CcaiApi.StreamingAnalyzeContentResponse finalVAResponse;
        private CcaiApi.StreamingAnalyzeContentResponse finalDTMFResponse;
        private CcaiApi.StreamingAnalyzeContentResponse callEndResponse;
        private CcaiApi.StreamingAnalyzeContentResponse aaResponse;

        public ResponseBuilder callStartResponse(CcaiApi.StreamingAnalyzeContentResponse callStartResponse) {
            this.callStartResponse = callStartResponse;
            return this;
        }

        public ResponseBuilder startOfInputResponse(CcaiApi.StreamingAnalyzeContentResponse startOfInputResponse) {
            this.startOfInputResponse = startOfInputResponse;
            return this;
        }

        public ResponseBuilder partialRecognitionResponse(CcaiApi.StreamingAnalyzeContentResponse partialRecognitionResponse) {
            this.partialRecognitionResponse = partialRecognitionResponse;
            return this;
        }

        public ResponseBuilder endOfInputResponse(CcaiApi.StreamingAnalyzeContentResponse endOfInputResponse) {
            this.endOfInputResponse = endOfInputResponse;
            return this;
        }

        public ResponseBuilder finalRecognitionResponse(CcaiApi.StreamingAnalyzeContentResponse finalRecognitionResponse) {
            this.finalRecognitionResponse = finalRecognitionResponse;
            return this;
        }

        public ResponseBuilder finalVAResponse(CcaiApi.StreamingAnalyzeContentResponse finalVAResponse) {
            this.finalVAResponse = finalVAResponse;
            return this;
        }

        public ResponseBuilder finalDTMFResponse(CcaiApi.StreamingAnalyzeContentResponse finalDTMFResponse) {
            this.finalDTMFResponse = finalDTMFResponse;
            return this;
        }

        public ResponseBuilder callEndResponse(CcaiApi.StreamingAnalyzeContentResponse callEndResponse) {
            this.callEndResponse = callEndResponse;
            return this;
        }

        public ResponseBuilder aaResponse(CcaiApi.StreamingAnalyzeContentResponse aaResponse) {
            this.aaResponse = aaResponse;
            return this;
        }

        public Response build() {
            Response response = new Response();
            response.callStartResponse = this.callStartResponse;
            response.startOfInputResponse = this.startOfInputResponse;
            response.partialRecognitionResponse = this.partialRecognitionResponse;
            response.endOfInputResponse = this.endOfInputResponse;
            response.finalRecognitionResponse = this.finalRecognitionResponse;
            response.finalVAResponse = this.finalVAResponse;
            response.finalDTMFResponse = this.finalDTMFResponse;
            response.callEndResponse = this.callEndResponse;
            response.aaResponse = this.aaResponse;
            return response;
        }
    }
}
