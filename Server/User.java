package Server;

import java.net.InetAddress;

public class User {
    private InetAddress address;
    private int port;
    private Boolean isBanned;

    public User(InetAddress address, int port, boolean isBanned) {
        this.address = address;
        this.port = port;
        this.isBanned = isBanned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (port != user.port) return false;
        return address.equals(user.address);
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port;
        return result;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isBanned() {
        return isBanned;
    }
}
