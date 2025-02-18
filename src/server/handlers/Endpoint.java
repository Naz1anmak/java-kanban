package server.handlers;

public enum Endpoint {
    GET_TASKS("GET", "/tasks"),
    GET_TASK_BY_ID("GET", "/tasks/{id}"),
    CREATE_OR_UPDATE_TASK("POST", "/tasks"),
    DELETE_TASK("DELETE", "/tasks/{id}"),

    GET_SUBTASKS("GET", "/subtasks"),
    GET_SUBTASK_BY_ID("GET", "/subtasks/{id}"),
    CREATE_OR_UPDATE_SUBTASK("POST", "/subtasks"),
    DELETE_SUBTASK("DELETE", "/subtasks/{id}"),

    GET_EPICS("GET", "/epics"),
    GET_EPIC_BY_ID("GET", "/epics/{id}"),
    GET_EPIC_SUBTASKS("GET", "/epics/{id}/subtasks"),
    CREATE_OR_UPDATE_EPIC("POST", "/epics"),
    DELETE_EPIC("DELETE", "/epics/{id}"),

    GET_HISTORY("GET", "/history"),
    GET_PRIORITIZED("GET", "/prioritized"),

    UNKNOWN("", "");

    private final String method;
    private final String path;

    Endpoint(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public static Endpoint endpointFromMethodAndPath(String method, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        for (Endpoint endpoint : Endpoint.values()) {
            String regex = endpoint.path.replace("{id}", "\\d+");

            if (endpoint.method.equals(method) && path.matches(regex)) {
                System.out.println("Возвращен эндпоинт " + endpoint);
                return endpoint;
            }
        }

        return Endpoint.UNKNOWN;
    }
}
