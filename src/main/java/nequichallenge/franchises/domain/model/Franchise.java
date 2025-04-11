package nequichallenge.franchises.domain.model;

import nequichallenge.franchises.domain.exception.FranchiseNameEmptyException;

import java.util.List;

public class Franchise {
    private Integer id;
    private String name;
    private List<Branch> branches;

    public Franchise() {
    }

    public Franchise(Integer id, String name, List<Branch> branches) {
        this.id = id;
        setName(name);
        this.branches = branches;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new FranchiseNameEmptyException();
        }
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

}
