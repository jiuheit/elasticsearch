/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.core.ccr.action;

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.support.master.MasterNodeReadRequest;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.xpack.core.ccr.AutoFollowMetadata.AutoFollowPattern;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class GetAutoFollowPatternAction
    extends Action<GetAutoFollowPatternAction.Request, GetAutoFollowPatternAction.Response, GetAutoFollowPatternAction.RequestBuilder> {

    public static final String NAME = "cluster:admin/xpack/ccr/auto_follow_pattern/get";
    public static final GetAutoFollowPatternAction INSTANCE = new GetAutoFollowPatternAction();

    private GetAutoFollowPatternAction() {
        super(NAME);
    }

    @Override
    public Response newResponse() {
        return new Response();
    }

    @Override
    public RequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new RequestBuilder(client);
    }

    public static class Request extends MasterNodeReadRequest<Request> {

        private String leaderCluster;

        public Request() {
        }

        @Override
        public ActionRequestValidationException validate() {
            return null;
        }

        public String getLeaderCluster() {
            return leaderCluster;
        }

        public void setLeaderCluster(String leaderCluster) {
            this.leaderCluster = leaderCluster;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            this.leaderCluster = in.readOptionalString();
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeOptionalString(leaderCluster);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Request request = (Request) o;
            return Objects.equals(leaderCluster, request.leaderCluster);
        }

        @Override
        public int hashCode() {
            return Objects.hash(leaderCluster);
        }
    }

    public static class Response extends ActionResponse implements ToXContentObject {

        private Map<String, AutoFollowPattern> autoFollowPatterns;

        public Response(Map<String, AutoFollowPattern> autoFollowPatterns) {
            this.autoFollowPatterns = autoFollowPatterns;
        }

        public Response() {
        }

        public Map<String, AutoFollowPattern> getAutoFollowPatterns() {
            return autoFollowPatterns;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            autoFollowPatterns = in.readMap(StreamInput::readString, AutoFollowPattern::new);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeMap(autoFollowPatterns, StreamOutput::writeString, (out1, value) -> value.writeTo(out1));
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            for (Map.Entry<String, AutoFollowPattern> entry : autoFollowPatterns.entrySet()) {
                builder.startObject(entry.getKey());
                entry.getValue().toXContent(builder, params);
                builder.endObject();
            }
            builder.endObject();
            return builder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Response response = (Response) o;
            return Objects.equals(autoFollowPatterns, response.autoFollowPatterns);
        }

        @Override
        public int hashCode() {
            return Objects.hash(autoFollowPatterns);
        }
    }

    public static class RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder> {

        RequestBuilder(ElasticsearchClient client) {
            super(client, INSTANCE, new Request());
        }
    }

}
