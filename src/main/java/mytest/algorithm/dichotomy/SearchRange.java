package mytest.algorithm.dichotomy;

public class SearchRange {
    // 二分法，找左起点和右结束点，两次二分查找
    public int[] searchRange(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }
        int l = 0;
        int r = nums.length - 1;
        int start = -1;
        int end = -1;
        while (r >= l) {
            int mid = (r + l) / 2;
            // 找到其中一个target了,继续往左侧搜
            if (nums[mid] == target) {
                start = mid;
                r = mid - 1;
                continue;

            }
            // target 在右侧
            if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }

        l = 0;
        r = nums.length - 1;
        while (r >= l) {
            int mid = (r + l) / 2;
            // 找到其中一个target了,继续往右侧搜
            if (nums[mid] == target) {
                end = mid;
                l = mid + 1;
                continue;

            }
            // target 在右侧
            if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return new int[]{start, end};
    }

    // 二分法找到target，左右搜索
    public int[] searchRange2(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }
        int l = 0;
        int r = nums.length - 1;
        int p = -1;
        while (r >= l) {
            int mid = (r + l) / 2;
            // 找到其中一个target了
            if (nums[mid] == target) {
                p = mid;
                break;
            }
            // target 在右侧
            if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        // 没有对应值
        if (p == -1) {
            return new int[]{-1, -1};
        }

        // 往左右找第一个不等于target的位置
        l = p;
        r = p;
        while (l >= 0 && nums[l] == target) {
            l--;
        }
        while (r < nums.length && nums[r] == target) {
            r++;
        }
        return new int[]{l + 1, r - 1};
//        int start = -1;
//        int end = -1;
//        while (l >= 0) {
//            // 第一个元素是target
//            if (l == 0 && nums[l] == target) {
//                start = 0;
//                break;
//            }
//            // 不是target的后一个是起始位置
//            if (nums[l] != target) {
//                start = l + 1;
//                break;
//            }
//            l--;
//        }
//        while (r < nums.length) {
//            if (r == nums.length - 1 && nums[r] == target) {
//                end = nums.length - 1;
//                break;
//            }
//            // 不是target的前一个是结束位置
//            if (nums[r] != target) {
//                end = r - 1;
//                break;
//            }
//            r++;
//        }
//        return new int[]{start, end};
    }

    // 指针法
    public int[] searchRange3(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }

        int p = 0;
        int start = -1;
        int end = -1;
        while (p < nums.length) {
            // 找到了第一个
            if (start == -1 && nums[p] == target) {
                start = p;
            }
            // 结束了
            if (start > -1 && nums[p] != target) {
                end = p - 1;
                break;
            }
            p++;
        }
        if (start > -1 && end == -1) {
            end = nums.length - 1;
        }

        return new int[]{start, end};
    }
}
