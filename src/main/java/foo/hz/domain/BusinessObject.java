package foo.hz.domain;

import java.io.Serializable;
import java.util.UUID;

public final class BusinessObject implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String id = UUID.randomUUID().toString();
    private final String owner;

    public BusinessObject(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BusinessObject)) {
            return false;
        }
        BusinessObject other = (BusinessObject) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BusinessObject [id=" + id + ", owner=" + owner + "]";
    }
}
