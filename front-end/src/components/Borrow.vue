<template>
    <el-scrollbar height="100%" style="width: 100%;">

        <!-- 标题和搜索框 -->
        <div style="margin-top: 20px; margin-left: 40px; font-size: 2em; font-weight: bold;">
            借书记录查询
            <el-input v-model="toSearch" :prefix-icon="Search"
                style=" width: 15vw;min-width: 150px; margin-left: 30px; margin-right: 30px; float: right; ;"
                clearable />
        </div>

        <!-- 查询框 -->
        <div style="width:30%;margin:0 auto; padding-top:5vh;">

            <el-input v-model="this.cardID" style="display:inline; " placeholder="输入借书证ID"></el-input>
            <br>
            <el-button style="align-items: center; margin-top:20px;" type="primary" @click="QueryBorrows">查询</el-button>

        </div>

        <!-- 结果表格 -->
        <el-table v-if="isShow" :data="fitlerTableData" height="600"
            :default-sort="{ prop: 'borrowTime', order: 'ascending' }" :table-layout="'auto'"
            style="width: 100%; margin-left: 50px; margin-top: 30px; margin-right: 50px; max-width: 80vw;">
            <el-table-column prop="cardID" label="借书证ID" />
            <el-table-column prop="bookID" label="图书ID" sortable />
            <el-table-column prop="borrowTime" label="借出时间" sortable />
            <el-table-column prop="returnTime" label="归还时间" sortable />
        </el-table>

    </el-scrollbar>
</template>

<script>
import axios from 'axios';
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';

export default {
    data() {
        return {
            isShow: false, // 结果表格展示状态
            tableData: [],
            type: '',
            cardID: '', // 待查询内容(对某一借书证号进行查询)
            toSearch: '', // 待搜索内容(对查询到的结果进行搜索)
            Search,
        }
    },
    computed: {
        fitlerTableData() { // 搜索规则
            return this.tableData.filter(
                (tuple) =>
                    (this.toSearch == '') || // 搜索框为空，即不搜索
                    tuple.bookID == this.toSearch || // 图书号与搜索要求一致
                    tuple.borrowTime.toString().includes(this.toSearch) || // 借出时间包含搜索要求
                    tuple.returnTime.toString().includes(this.toSearch) // 归还时间包含搜索要求
            )
        }
    },
    methods: {
        reflect(time) {
            if (time === 0) {
                return '未归还';
            }

            // 20250304092613 -> 2025年03月04日 09:26:13
            let t = time.toString();
            let year = t.substring(0, 4);
            let month = t.substring(4, 6);
            let day = t.substring(6, 8);
            let hour = t.substring(8, 10);
            let minute = t.substring(10, 12);
            let second = t.substring(12, 14);
            return year + '年' + month + '月' + day + '日 '+ hour + ':' + minute + ':' + second;
        },
        async QueryBorrows() {
            this.tableData = [] // 清空列表

            let response = axios.get('/borrow?type=records&cardId='+this.cardID)
               .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error);
                    } else {
                        let borrows = response.data.records // 获取响应负载
                        let tempData;
                        borrows.forEach(borrow => { // 对于每一个借书记录
                            tempData = {
                                cardID: borrow.cardID,
                                bookID: borrow.bookID,
                                borrowTime: this.reflect(borrow.borrowTime),
                                returnTime: this.reflect(borrow.returnTime)
                            }
                            this.tableData.push(tempData) // 将它加入到列表项中
                        });
                        this.isShow = true // 显示结果列表
                    }
               })
        }
    }
}
</script>