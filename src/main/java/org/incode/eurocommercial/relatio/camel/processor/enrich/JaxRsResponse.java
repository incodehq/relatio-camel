package org.incode.eurocommercial.relatio.camel.processor.enrich;

import javax.ws.rs.core.Response;

public interface JaxRsResponse {

    int getStatus();

    <T> T readEntity(final Class<T> entityType);

    class Default implements JaxRsResponse {

        private final Response response;

        public Default(final Response response) {
            this.response = response;
        }

        @Override
        public int getStatus() {
            return response.getStatus();
        }

        @Override
        public <T> T readEntity(final Class<T> entityType) {
            return response.readEntity(entityType);
        }
    }

    class ForTesting implements JaxRsResponse {

        private final int status;
        private final Object entity;

        public ForTesting(final int status, final Object entity) {
            this.status = status;
            this.entity = entity;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public <T> T readEntity(final Class<T> entityType) {
            return (T) entity;
        }
    }

}
