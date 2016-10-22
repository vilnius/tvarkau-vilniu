package lt.vilnius.tvarkau.backend;

public enum ApiMethod {

    LOGIN(1),
    REGISTER(2),
    LOGOUT(3),
    GET_PROBLEM_TYPES(4),
    NEW_PROBLEM(5),
    GET_PROBLEMS(6),
    GET_REPORT(7);

    private final int id;

    ApiMethod(int id) {
        this.id = id;
    }

    public int getId() { return id; }
}
