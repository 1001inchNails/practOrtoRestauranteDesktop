package com.example.practortorestaurantedesktopp.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDBManager {
    private static final String CONNECTION_STRING = "mongodb+srv://dccAtlMongoC_S:1001%%wWqq4904@clusterbuster.bl5p1.mongodb.net/?retryWrites=true&w=majority&appName=ClusterBuster";
    private static final String DATABASE_NAME = "mongofichas";

    private MongoDBManager() {}

    public static boolean actualizarDocumento(String coleccion, String campoFiltro, Object valorFiltro,
                                              String campoActualizacion, Object valorActualizacion) {
        MongoClient mongoClient = null;
        try {
            // Crear cliente MongoDB con configuración de conexión
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);

            // Obtener la colección y realizar la actualización
            MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(coleccion);

            Bson filter = Filters.eq(campoFiltro, valorFiltro);
            Bson update = Updates.set(campoActualizacion, valorActualizacion);

            UpdateResult result = collection.updateOne(filter, update);
            return result.getModifiedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar documento: " + e.getMessage());
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public static boolean insertarDocumento(String coleccion, Document documento) {
        MongoClient mongoClient = null;
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);

            MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(coleccion);
            collection.insertOne(documento);
            return true;

        } catch (Exception e) {
            System.err.println("Error al insertar documento: " + e.getMessage());
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public static Document buscarDocumento(String coleccion, String campoFiltro, Object valorFiltro) {
        MongoClient mongoClient = null;
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);

            MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(coleccion);
            Bson filter = Filters.eq(campoFiltro, valorFiltro);

            return collection.find(filter).first();

        } catch (Exception e) {
            System.err.println("Error al buscar documento: " + e.getMessage());
            return null;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public static boolean eliminarDocumento(String coleccion, String campoFiltro, Object valorFiltro) {
        MongoClient mongoClient = null;
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                    .serverApi(serverApi)
                    .build();

            mongoClient = MongoClients.create(settings);

            MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE_NAME).getCollection(coleccion);
            Bson filter = Filters.eq(campoFiltro, valorFiltro);

            DeleteResult result = collection.deleteOne(filter);
            return result.getDeletedCount() > 0;

        } catch (Exception e) {
            System.err.println("Error al eliminar documento: " + e.getMessage());
            return false;
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }
}