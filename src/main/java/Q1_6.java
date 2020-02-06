import java.util.*;

/**
 * 1.6 饮料供货
 * STC(Smart Tea Corp.)负责提供饮料，研究院的阿姨统计出了每种饮料的满意度。已知STC每天总供货量为 V，每种饮料
 * 单个容量是 2 的幂(如2^3 = 8 L)，每种饮料有单独的购买容量上限。
 * 统计数据中用饮料名字，容量，数量和满意度描述每一种饮料。
 */
public class Q1_6 {
    public static void main(String[] args){
        Drink wanglaoji = new Drink("王老吉", 4, 4, 20.0);
        Drink cola = new Drink("可乐", 2, 5, 30.0);
        Drink tea = new Drink("茶", 8, 6, 50.0);
        Drink[] drinks = {wanglaoji, cola, tea};

        int maxVolume = 20;
        Solution1 solution1 = new Solution1(drinks, maxVolume);

        //Solution2 solution2 = new Solution2(drinks, maxVolume);

        //Solution3 solution3 = new Solution3(drinks, maxVolume);
    }

    /**
     * 首先数学建模，
     * 用(Ni, Vi, Qi, Si, Bi)表示饮料，
     * 五个变量分别表示 名字(Name)，单个容量(Volume)，可购买的最大数量(Quantity)，满意度(Satisfaction)
     * 以及实际购买量(Buy)，i表示第 i 饮料
     *
     * 这样总购买容量为
     * ∑(V*B) (i=0 - i=n-1)
     * 总满意度为
     * ∑(S*B) (i=0 - i=n-1)
     * 我们要在保证∑(V*B) = V(最大存货量)的情况下使∑(S*B)最大
     *
     * 所以
     * Opt(V, i) = max {k*Si + Opt(V'-k*Vi, i+1}} (k=0,1...,Qi, i=0,1...,n-1)
     * 即对于给定最大容量以及第 i 种饮料，最优解为买 k 个i饮料的满意度再加上买剩下饮料满意度的最大值
     * 边界条件为
     * Opt(n, 0) = 0, 即已达到最大购买容量，返回0
     * Opt(x, n) = -INF (x!=0), 即如果未达到最大购买容量把最优化结果初值设为负无穷。
     */
    private static class Solution1{

        private Drink[] drinks; // 饮料信息数组
        private int kindOfDrinks; // 饮料种类
        private int maxVolume; // 最大购买容量

        public Solution1(Drink[] drinks, int maxStorage){
            this.kindOfDrinks = drinks.length;

            this.drinks = new Drink[this.kindOfDrinks];
            System.arraycopy(drinks, 0, this.drinks, 0, this.kindOfDrinks);

            this.maxVolume = maxStorage;

            printResult(run(maxVolume, 0));
        }

        private double run(int maxVolume, int indexOfDrink){
            // 当已经达到最大购买量时返回0
            if (maxVolume == 0){
                return 0;
            }

            // 超过数组范围
            if (indexOfDrink >= this.kindOfDrinks){
                return 0;
            }

            double maxSatisfaction = -Double.MAX_VALUE;
            int optBuyQuantity = 0;

            Drink currentDrink = drinks[indexOfDrink];

            // 将购买量从0到该饮料最大值检索出合适k值
            for (int buyQuantity=0;buyQuantity<=currentDrink.getMaxQuantity();buyQuantity++){

                double satisfaction = 0;

                if (maxVolume - buyQuantity*currentDrink.getVolume() < 0){
                    break;
                }

                // 统计剩下饮料的最大满意度
                satisfaction = buyQuantity*currentDrink.getSatisfaction() +
                        run(maxVolume - buyQuantity*currentDrink.getVolume(), indexOfDrink+1);

                if (satisfaction > maxSatisfaction){
                    maxSatisfaction = satisfaction;
                    currentDrink.setBuyQuantity(buyQuantity);
                    optBuyQuantity = buyQuantity;
                }
            }

            currentDrink.setBuyQuantity(optBuyQuantity);
            return maxSatisfaction;
        }

        private void printResult(double satisfaction){
            System.out.println("最大满意度为： " + satisfaction);

            System.out.println("购买方案为: ");
            for (Drink drink : drinks){
                System.out.println(drink.getName() + " 买 " + drink.getBuyQuantity() + " 个，共 " +
                        drink.getVolume()*drink.getBuyQuantity() + " L");
            }
        }
    }

    /**
     * 利用缓存进一步优化
     */
    private static class Solution2{

        private double[][] satisfactionMemory; // 缓存数组，butListMemory[a][b]表示剩余容量为a时购买饮料b满意度最多能达到多少
        private Drink[] drinks;
        private int maxVolume;
        private int kindOfDrink;

        public Solution2(Drink[] drinks, int maxVolume){
            this.kindOfDrink = drinks.length;

            this.drinks = new Drink[kindOfDrink];
            System.arraycopy(drinks, 0, this.drinks, 0, this.kindOfDrink);

            this.maxVolume = maxVolume;

            this.satisfactionMemory = new double[maxVolume+1][kindOfDrink+1];
            for (int i=0;i<=maxVolume;i++){
                for (int j=0;j<=kindOfDrink;j++){
                    this.satisfactionMemory[i][j] = -1;
                }
            }

            print(run(maxVolume, 0));
        }

        private double run(int maxVolume, int indexOfDrink){
            if (indexOfDrink == kindOfDrink){
                if (maxVolume == 0){
                    return 0;
                }else{
                    return -Double.MAX_VALUE;
                }
            }

            if (maxVolume < 0){
                return  -Double.MAX_VALUE;
            }else if(maxVolume == 0){
                return 0;
            }else if(satisfactionMemory[maxVolume][indexOfDrink] != -1){
                return satisfactionMemory[maxVolume][indexOfDrink];
            }

            double maxSatisfaction = -Double.MAX_VALUE;
            Drink currentDrink = drinks[indexOfDrink];

            int optBuyQuantity = -1;

            for (int buyQuantity=0;buyQuantity<=currentDrink.getMaxQuantity();buyQuantity++){
                double temp = run(maxVolume - buyQuantity * currentDrink.getVolume(),
                        indexOfDrink + 1);

                if (temp != -Double.MAX_VALUE){
                    temp += buyQuantity * currentDrink.getSatisfaction();
                    if (temp > maxSatisfaction){
                        maxSatisfaction = temp;
                        currentDrink.setBuyQuantity(buyQuantity);
                        optBuyQuantity = buyQuantity;
                    }
                }
            }

            return satisfactionMemory[maxVolume][indexOfDrink] = maxSatisfaction;
        }

        private void print(double satisfaction){
            System.out.println("最大满意度为： " + satisfaction);

            System.out.println("购买方案为: ");
            int i = 0;
            for (Drink drink : drinks){
                System.out.println(drink.getName() + " 买 " + drink.getBuyQuantity() + " 个，共 " +
                        drink.getVolume()*drink.getBuyQuantity() + " L");
            }
        }
    }

    /**
     * 简便算法？
     * 令每一个饮料的 满意度 / 每瓶升数 为每升可提供满意度
     * 如可乐2升一瓶可以提供30点满意度，每升可提供15点满意度
     * 这样只需要用贪心算法优先采购提供最大单位满意度的饮料即可
     */
    private static class Solution3{

        private Drink[] drinks;
        private int maxVolume;
        private int kindOfDrink;

        private Map<Drink, Double> satisfactionUnit;
        private double maxSatisfaction = 0;

        public Solution3(Drink[] drinks, int maxVolume){
            this.kindOfDrink = drinks.length;

            this.drinks = new Drink[kindOfDrink];
            System.arraycopy(drinks, 0, this.drinks, 0, this.kindOfDrink);

            this.maxVolume = maxVolume;

            satisfactionUnit = new HashMap<>();
            for (Drink drink : drinks){
                satisfactionUnit.put(drink, drink.getSatisfaction()/drink.getVolume());
            }

            // 按单位满意度排序
            Arrays.sort(this.drinks, new Comparator<Drink>() {
                @Override
                public int compare(Drink o1, Drink o2) {
                    return satisfactionUnit.get(o2).compareTo(satisfactionUnit.get(o1));
                }
            });

            run();
            print();
        }

        private void run(){

            int index = 0;

            // 尽量先买单位满意度高的
            while (maxVolume > 0 && index < kindOfDrink){
                if (drinks[index].getMaxQuantity() > drinks[index].getBuyQuantity() &&
                        maxVolume - drinks[index].getVolume() >= 0){
                    drinks[index].setBuyQuantity(drinks[index].getBuyQuantity()+1);
                    maxVolume -= drinks[index].getVolume();
                    maxSatisfaction += drinks[index].getSatisfaction();
                }else{
                    index++;
                }
            }
        }

        private void print(){
            System.out.println("最大满意度为： " + maxSatisfaction);

            System.out.println("购买方案为: ");
            for (int i=0;i<kindOfDrink;i++)
                System.out.println(drinks[i].getName() + " 买 " + drinks[i].getBuyQuantity() + " 个，共 " +
                        drinks[i].getVolume()*drinks[i].getBuyQuantity() + " L");
        }
    }
}

class Drink{

    private final String name;
    private final int volume;
    private final int maxQuantity;
    private final double satisfaction;
    private int buyQuantity;

    public Drink(String name, int volume, int maxQuantity, double satisfaction){
        if (name == null || !isPowerOf2(volume) || maxQuantity <=0 || satisfaction <= 0){
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.volume = volume;
        this.maxQuantity = maxQuantity;
        this.satisfaction = satisfaction;
    }

    public void setBuyQuantity(int buyQuantity){
        this.buyQuantity = buyQuantity;
    }

    /**
     * 判断一个数是否为2的幂，2的幂的二进制形式仅有一位为1所以如果二进制多于1位大于1就不是
     */
    private boolean isPowerOf2(int num){
        if (num <= 0){
            return false;
        }

        int count = 0;
        while (num != 0){
            if ((num & 1) == 1){
                count ++;
            }
            if (count > 1){
                return false;
            }
            num = num >> 1;
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }
}
