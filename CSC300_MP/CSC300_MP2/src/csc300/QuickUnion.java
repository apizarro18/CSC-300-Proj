package csc300;

public class QuickUnion {
    // TODO: Implementing Weighted QU will require changes here
    private int[] id;
    private int components;

    // TODO: Implementing Weighted QU will require changes here
    public QuickUnion(int N) {
        this.id = new int[N];
        for (int i = 0; i < N; i++) {
            this.id[i] = i;
        }
        this.components = N;
    }

    public int getSize() {
        return this.id.length;
    }

    // TODO: Implementing Path Comp. QU will require changes here
    private int root(int i) {
        while (i != this.id[i]) {
            i = this.id[i];
        }
        return i;
    }

    public boolean connected(int p, int q) {
        return this.root(p) == this.root(q);
    }

    // TODO: Implementing Weighted QU will require changes here
    public void union(int p, int q) {
        int i = this.root(p);
        int j = this.root(q);

        if (i == j) {
            return;
        }

        this.id[i] = j;
        this.components -= 1;
    }

    public int find(int p) {
        return this.id[p];
    }

    public int count() {
        return this.components;
    }
}
