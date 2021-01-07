import java.util.*;
public class CSP {
    public static void main(String[] args) {
        CSP m = new CSP();
        System.out.println(m.solve());
    }
    // 下面这组变量是DINIC跑最大流的基础变量。n是点数，m是边数
    int N = 20020, M = 300010, inf = (int) 1e9;
    // h e ne 3个数组 是数组模拟邻接表的建图方式，加边函数参加ADD
    // w 代表 残留网络的剩余容量。 d 是对所有点建立分层图，维护层数
    // cur 是当前层优化的数组 S代表源点 T代表汇点
    int[] h = new int[N], cur = new int[N], d = new int[N];
    int[] e = new int[M], ne = new int[M], w = new int[M];
    int S, T, idx = 0;

    int[][] goods;
    void add(int a, int b, int c) {
        e[idx] = b; w[idx] = c; ne[idx] = h[a]; h[a] = idx++;
        e[idx] = a; w[idx] = 0; ne[idx] = h[b]; h[b] = idx++;
    }
    private long solve() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(), m = sc.nextInt();
        goods = new int[n+1][];
        S = 0; T = 2 * n + 1;
        Arrays.fill(h, -1);

        long tot = 0; // 存所有正权点的和
        for (int i = 1; i <= n; i++) {
            goods[i] = new int[]{sc.nextInt(), sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt()};
            int v1 = cal(goods[i]), v2 = cal2(goods[i]) - v1;
            // i + n 是第二类点，i是第一类点
            add(i + n, i, inf);
            tot += Math.max(0, v1) + Math.max(0, v2);
            if (v1 > 0) add(S, i, v1);
            else if (v1 < 0) add(i, T, -v1);

            if (v2 > 0) add(S, i+n, v2);
            else if (v2 < 0) add(i+n, T, -v2);
        }

        for (int i = 0; i < m; i++) {
            int type = sc.nextInt(), x = sc.nextInt(), y = sc.nextInt(), yy = y + n;
            if (type == 1) {
                add(y, x, inf);
            } else {
                add(yy, x, inf);
            }
        }

        // 正权和-最小割 为 最大闭合子图的解
        return tot - dinic();
    }

    // dinic 模板
    private long dinic() {
        long r = 0, flow;
        while (bfs()) while ((flow = find(S, inf)) != 0) r += flow;
        return r;
    }
    // dinic find 函数模板，带当前层优化
    private int find(int u, int limit) {
        if (u == T) return limit;
        int flow = 0;
        for (int i = cur[u]; i != -1 && flow < limit; i = ne[i]) {
            int j = e[i];
            cur[u] = i;
            if (d[j] == d[u] + 1 && w[i] > 0) {
                int v = find(j, Math.min(w[i], limit - flow));
                if (v == 0) d[j] = -1;
                w[i] -= v; w[i ^ 1] += v; flow += v;
            }
        }
        return flow;
    }

    // dinic bfs 建分层图模板
    private boolean bfs() {
        Arrays.fill(d, -1);
        cur = h.clone();
        Queue<Integer> q = new LinkedList<>();
        q.offer(S); d[S] = 0;
        while (!q.isEmpty()) {
            int a = q.poll();
            for (int i = h[a]; i != -1; i = ne[i]) {
                int b = e[i];
                if (d[b] == -1 && w[i] > 0) {
                    d[b] = d[a] + 1;
                    if (b == T) return true;
                    q.offer(b);
                }
            }
        }
        return false;
    }

    // 二次函数求值
    int y(int a, int b, int c, int x) {
        return a * x * x + b * x + c;
    }
    // 第二类点求最大值
    private int cal2(int[] g) {
        return Math.max(y(g[2],g[3],g[4],g[0]), y(g[2],g[3],g[4],g[1]));
    }
    // 第一类点求最大值
    private int cal(int[] g) {
        int a = g[2], b = g[3], c = g[4], l = g[0], r = g[1];
        if (l + 1 > r - 1) return 0;
        if (a > 0) return Math.max(y(a,b,c,l+1), y(a,b,c,r-1));
        else if (a < 0) {
            double maxX = -b / 2.0 / a;
            if (maxX <= l) return y(a, b, c, l + 1);
            else if (maxX >= r) return y(a, b, c, r - 1);
            return Math.max(y(a, b, c, (int)Math.floor(maxX)), y(a, b, c, (int)Math.ceil(maxX)));
        }
        return y(a, b, c, b > 0 ? r - 1 : l + 1);
    }
}