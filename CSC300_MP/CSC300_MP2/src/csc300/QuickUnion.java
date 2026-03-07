package csc300;

public class QuickUnion {
    // TODO: STEP 1: Implementing Weighted QU will require changes here
    private int[] id;
    private int[] sizes;
    private int components;

    // TODO: STEP 1: Implementing Weighted QU will require changes here
    public QuickUnion(int N) {
        this.id = new int[N];
        this.sizes = new int[N];
        for (int i = 0; i < N; i++) {
            this.id[i] = i;
            this.sizes[i] = 1;
        }
        this.components = N;
    }

    public int getSize() {
        return this.id.length;
    }

    // TODO: STEP 1: Implementing Path Comp. QU will require changes here
    private int root(int i) {
        int root = i;
        while (root != this.id[root]) {
            root = this.id[root];
        }

        while (i != root) {
            int temp = this.id[i];
            this.id[i] = root;
            i = temp;
        }
        return root;
    }

    // TODO: you might need to change sth here.
    public boolean connected(int p, int q) {
        return this.root(p) == this.root(q);
    }

    // TODO: STEP 1: Implementing Weighted QU will require changes here
    public void union(int p, int q) {
        int i = this.root(p);
        int j = this.root(q);

        if (i == j) {
            return;
        }
        if (sizes[i] > sizes[j]) {
            this.id[j] = i;
            this.sizes[i] += this.sizes[j];
        }
        else {
            this.id[i] = j;
            this.sizes[j] += this.sizes[i];
        }
        this.components -= 1;
    }

    // TODO: you might need to change sth here.
    public int find(int p) {
        return this.id[p];
    }

    public int count() {
        return this.components;
    }
}