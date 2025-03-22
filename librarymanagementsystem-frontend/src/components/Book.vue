<template>
    <el-scrollbar height="100%" style="width: 100%;">
        <!-- 标题 -->
        <div style="margin-top: 20px; margin-left: 40px; font-size: 2em; font-weight: bold; ">图书管理</div>

        <!-- 功能卡片显示区 -->
        <div style="display: flex;flex-wrap: wrap; justify-content: start;">

            <!-- 功能卡片 -->
            <el-button class="cardBox" @click="newBorrowVisible=true">
                <!-- 借书卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">我要借书</div>
                    <el-divider />
                    <el-icon size="120px" color="rgb(121.3, 187.1, 255)" style="margin-top: 0.1rem;"><Notebook /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="newReturnVisible=true">
                <!-- 还书卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">我要还书</div>
                    <el-divider />
                    <el-icon size="120px" color="rgb(148.6, 212.3, 117.1)" style="margin-top: 0.1rem;"><DocumentChecked /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="queryVisible=true">
                <!-- 查询图书卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">查询图书</div>
                    <el-divider />
                    <el-icon size="120px" color="#606266" style="margin-top: 0.1rem;"><Search /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="storeBookVisible=true">
                <!-- 图书入库卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">图书入库</div>
                    <el-divider />
                    <el-icon size="120px" color="pink" style="margin-top: 0.1rem;"><Collection /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="batchStoreBookVisible=true">
                <!-- 图书批量入库卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">图书批量入库</div>
                    <el-divider />
                    <el-icon size="120px" color="rgb(237.5, 189.9, 118.5)" style="margin-top: 0.1rem;"><Management /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="deleteBookVisible=true">
                <!-- 删除图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">删除图书</div>
                    <el-divider />
                    <el-icon size="120px" color="rgb(196, 86.4, 86.4)" style="margin-top: 0.1rem;"><FolderDelete /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="modifyStockVisible=true">
                <!-- 修改库存图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">图书库存修改</div>
                    <el-divider />
                    <el-icon size="120px" color="teal" style="margin-top: 0.1rem;"><EditPen /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="modifyInfoVisible=true">
                <!-- 修改信息图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 23px; font-weight: bold;">图书信息修改</div>
                    <el-divider />
                    <el-icon size="120px" color="black" style="margin-top: 0.1rem;"><InfoFilled /></el-icon>
                </div>
            </el-button>

        </div>


        <!-- 借书对话框 -->
        <el-dialog v-model="newBorrowVisible" title="我要借书" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书号：
                <el-input v-model="newBorrowInfo.bookId" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                卡号：
                <el-input v-model="newBorrowInfo.cardId" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                借书时间：
                <el-date-picker v-model="newBorrowInfo.borrowTime" type="datetime" placeholder="选择日期" style="width: 12.5vw;" />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="newBorrowVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmNewBorrow"
                        :disabled="newBorrowInfo.bookId.length === 0 || newBorrowInfo.cardId.length === 0 || newBorrowInfo.borrowTime.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 还书对话框 -->
        <el-dialog v-model="newReturnVisible" title="我要还书" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书号：
                <el-input v-model="newReturnInfo.bookId" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                卡号：
                <el-input v-model="newReturnInfo.cardId" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                还书时间：
                <el-date-picker v-model="newReturnInfo.returnTime" type="datetime" placeholder="选择日期" style="width: 12.5vw;" />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="newReturnVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmNewReturn"
                        :disabled="newReturnInfo.bookId.length === 0 || newReturnInfo.cardId.length === 0 || newReturnInfo.returnTime.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 查询对话框 -->
        <el-dialog v-model="queryVisible" title="查询图书" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                类别：
                <el-input v-model="queryInfo.category" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书名：
                <el-input v-model="queryInfo.title" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                出版社：
                <el-input v-model="queryInfo.press" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                最小年份：
                <el-date-picker v-model="queryInfo.minYear" type="year" placeholder="选择年份" style="width: 12.5vw;" />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                最大年份：
                <el-date-picker v-model="queryInfo.maxYear" type="year" placeholder="选择年份" style="width: 12.5vw;" />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                作者：
                <el-input v-model="queryInfo.author" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                最低价格：
                <el-input v-model="queryInfo.minPrice" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                最高价格：
                <el-input v-model="queryInfo.maxPrice" style="width: 12.5vw;" clearable />    
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="queryVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmQuery"
                        :disabled="queryInfo.category.length === 0 && queryInfo.title.length === 0 && queryInfo.press.length === 0 && queryInfo.minYear.length === 0 && queryInfo.maxYear.length === 0 && queryInfo.author.length === 0 && queryInfo.minPrice.length === 0 && queryInfo.maxPrice.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 入库对话框 -->
        <el-dialog v-model="storeBookVisible" title="单本图书入库" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                类别：
                <el-input v-model="storeBookInfo.category" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书名：
                <el-input v-model="storeBookInfo.title" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                出版社：
                <el-input v-model="storeBookInfo.press" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                出版年份：
                <el-date-picker v-model="storeBookInfo.publish_year" type="year" placeholder="选择年份" style="width: 12.5vw;" />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                作者：
                <el-input v-model="storeBookInfo.author" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                价格：
                <el-input v-model="storeBookInfo.price" style="width: 12.5vw;" clearable />
            </div>
            <div style="margin-left: 2vw;   font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                库存：
                <el-input v-model="storeBookInfo.stock" style="width: 12.5vw;" clearable />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="storeBookVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmStore"
                        :disabled="storeBookInfo.category.length === 0 || storeBookInfo.title.length === 0 || storeBookInfo.press.length === 0 || storeBookInfo.publish_year.length === 0 || storeBookInfo.author.length === 0 || storeBookInfo.price.length === 0 || storeBookInfo.stock.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 批量入库对话框 -->
        <el-dialog v-model="batchStoreBookVisible" title="图书批量入库" width="50%" align-center>
            <el-table :data="batchStoreBookInfo" style="width: 100%">
            <el-table-column prop="category" label="类别" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.category"/>
                </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.title"/>
                </template>
            </el-table-column>
            <el-table-column prop="press" label="出版社" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.press"/>
                </template>
            </el-table-column>
            <el-table-column prop="publishYear" label="出版年份" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.publishYear"/>
                </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.author"/>
                </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.price"/>
                </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" style="width: 10%">
                <template #default="scope">
                <el-input v-model="scope.row.stock"/>
                </template>
            </el-table-column>
            <!-- 整行删除按钮 -->
            <el-table-column fixed="right" label="操作" style="width: 10%">
                <template #default="scope">
                <el-button link type="danger" size="small" @click.prevent="deleteRow(scope.$index)">
                    删除
                </el-button>
                </template>
            </el-table-column>
            </el-table>
            <el-button class="mt-4" style="width: 100%" @click="onAddItem">
                增加
            </el-button>

            <template #footer>
            <span>
                <el-button type="danger" @click="batchStoreBookVisible = false">取消</el-button>
                <el-button type="primary" @click="ConfirmBatchStore"
                :disabled="batchStoreBookInfo.length === 0">确定</el-button>
            </span>
            </template>
        </el-dialog>

        <!-- 删除图书对话框 -->
        <el-dialog v-model="deleteBookVisible" title="删除图书" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书号：
                <el-input v-model="toDelete" style="width: 12.5vw;" clearable />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="deleteBookVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmDelete"
                        :disabled="toDelete.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 修改库存对话框 -->
        <el-dialog v-model="modifyStockVisible" title="图书库存修改" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书号：
                <el-input v-model="modifyStockData.bookId" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                变化量：
                <el-input v-model="modifyStockData.delta" style="width: 12.5vw;" clearable />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="modifyStockVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmModifyStock"
                        :disabled="modifyStockData.bookId.length === 0 || modifyStockData.delta.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 修改信息对话框 -->
        <el-dialog v-model="modifyInfoVisible" title="图书信息修改" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书号：
                <el-input v-model="modifyInfoData.bookId" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                类别：
                <el-input v-model="modifyInfoData.category" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                书名：
                <el-input v-model="modifyInfoData.title" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                出版社：
                <el-input v-model="modifyInfoData.press" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                出版年份：
                <el-date-picker v-model="modifyInfoData.publish_year" type="year" placeholder="选择年份" style="width: 12.5vw;" />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                作者：
                <el-input v-model="modifyInfoData.author" style="width: 12.5vw;" clearable />
            </div>

            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                价格：
                <el-input v-model="modifyInfoData.price" style="width: 12.5vw;" clearable />
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="modifyInfoVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmModifyInfo"
                        :disabled="modifyInfoData.bookId.length === 0 || modifyInfoData.category.length === 0 || modifyInfoData.title.length === 0 || modifyInfoData.press.length === 0 || modifyInfoData.publish_year.length === 0 || modifyInfoData.author.length === 0 || modifyInfoData.price.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 结果表格 -->
        <template v-if="ShowQueryResults">
            <!-- 标题 -->
            <div style="text-align: center; font-size: 22px; font-weight: bold; margin-top: 20px;">
                图书查询结果
                <el-button type="primary" @click="ShowQueryResults = false" style="margin-left: 20px;">隐藏</el-button>
            </div>

            <el-table :data="queryResults" height="500"
                :default-sort="{ prop: 'bookId', order: 'ascending' }" :table-layout="'auto'"
                style="width: 100%; margin-left: 50px; margin-top: 30px; margin-right: 50px; max-width: 80vw;">
                <el-table-column prop="bookId" label="书号" />
                <el-table-column prop="category" label="类别" sortable />
                <el-table-column prop="title" label="书名" sortable />
                <el-table-column prop="press" label="出版社" sortable />
                <el-table-column prop="publishYear" label="出版年份" sortable />
                <el-table-column prop="author" label="作者" sortable />
                <el-table-column prop="price" label="价格" sortable />
                <el-table-column prop="stock" label="库存" sortable />
            </el-table>

        </template>

    </el-scrollbar>
</template>

<script>
import { Delete, Edit, EditPen, FolderDelete, InfoFilled, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
    data() {
        return {
            newBorrowVisible: false, // 新建借书对话框可见性
            newBorrowInfo: { // 新建借书信息
                bookId: '',
                cardId: '',
                borrowTime: ''
            },
            newReturnVisible: false, // 新建还书对话框可见性
            newReturnInfo: { // 新建还书信息
                bookId: '',
                cardId: '',
                returnTime: ''
            },
            queryVisible: false, // 查询图书对话框可见性
            queryInfo: {
                category: '',
                title: '',
                press: '',
                minYear: '',
                maxYear: '',
                author: '',
                minPrice: '',
                maxPrice: ''
            },
            queryResults: [], // 查询结果
            ShowQueryResults: false, // 是否显示查询结果
            storeBookVisible: false, // 图书入库对话框可见性
            storeBookInfo: { // 图书入库信息
                category: '',
                title: '',
                press: '',
                publish_year: '',
                author: '',
                price: '',
                stock: ''
            },
            batchStoreBookVisible: false, // 批量入库对话框可见性
            batchStoreBookInfo: [], // 批量入库文件列表
            deleteBookVisible: false, // 删除图书对话框可见性
            toDelete: '',
            modifyStockVisible: false, // 修改库存对话框可见性
            modifyStockData: {
                bookId: '',
                delta: ''
            },
            modifyInfoVisible: false, // 修改信息对话框可见性
            modifyInfoData: {
                bookId: '',
                category: '',
                title: '',
                press: '',
                publish_year: '',
                author: '',
                price: ''
            },
            action: '' // 具体操作
        }
    },
    methods: {
        deleteRow(index) {
            this.batchStoreBookInfo.splice(index, 1)
        },
        onAddItem() {
            this.batchStoreBookInfo.push({
                category: '',
                title: '',
                press: '',
                publishYear: '',
                author: '',
                price: '',
                stock: ''
            })
        },
        formatDateToString(date) {
            const year = date.getFullYear(); // 获取年份（4位）
            const month = String(date.getMonth() + 1).padStart(2, '0'); // 获取月份（补零）
            const day = String(date.getDate()).padStart(2, '0'); // 获取日期（补零）
            const hours = String(date.getHours()).padStart(2, '0'); // 获取小时（补零）
            const minutes = String(date.getMinutes()).padStart(2, '0'); // 获取分钟（补零）
            const seconds = String(date.getSeconds()).padStart(2, '0'); // 获取秒数（补零）

            // 拼接成 "YYYYMMDDHHmmss" 格式
            return `${year}${month}${day}${hours}${minutes}${seconds}`;
        },
        ConfirmNewBorrow() {
            // 将日期转换成字符串格式
            const borrowT = this.formatDateToString(this.newBorrowInfo.borrowTime)
            axios.post("/book", {
                action: "borrow",
                borrow: {
                    cardId: this.newBorrowInfo.cardId,
                    bookId: this.newBorrowInfo.bookId,
                    borrowTime: borrowT,
                    returnTime: '0'
                }
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.newBorrowVisible = false // 将对话框设置为不可见
                    this.newBorrowInfo.bookId = ''
                    this.newBorrowInfo.cardId = ''
                    this.newBorrowInfo.borrowTime = ''
                }
            })
        },
        ConfirmNewReturn() {
            const returnT = this.formatDateToString(this.newReturnInfo.returnTime)
            axios.post("/book", {
                action: "return",
                borrow: {
                    cardId: this.newReturnInfo.cardId,
                    bookId: this.newReturnInfo.bookId,
                    borrowTime: '0',
                    returnTime: returnT
                }
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.newReturnVisible = false // 将对话框设置为不可见
                    this.newReturnInfo.bookId = ''
                    this.newReturnInfo.cardId = ''
                    this.newReturnInfo.returnTime = ''
                }
            })
        },
        ConfirmQuery() {
            this.queryResults = [] // 清空查询结果

            let query = "/book?type=records"

            if (this.queryInfo.category.length > 0) {
                query += `&category=${this.queryInfo.category}`
            }
            if (this.queryInfo.title.length > 0) {
                query += `&title=${this.queryInfo.title}`
            }
            if (this.queryInfo.press.length > 0) {
                query += `&press=${this.queryInfo.press}`
            }
            if (this.queryInfo.minYear.length > 0) {
                query += `&min-publish-year=${this.queryInfo.minYear.getFullYear().toString()}`
            }
            if (this.queryInfo.maxYear.length > 0) {
                query += `&max-publish-year=${this.queryInfo.maxYear.getFullYear().toString()}`
            }
            if (this.queryInfo.author.length > 0) {
                query += `&author=${this.queryInfo.author}`
            }
            if (this.queryInfo.minPrice.length > 0) {
                query += `&minprice=${this.queryInfo.minPrice}`
            }
            if (this.queryInfo.maxPrice.length > 0) {
                query += `&maxprice=${this.queryInfo.maxPrice}`
            }

            let response = axios.get(query) // 向/card发出GET请求
                .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error) // 显示错误消息
                    } else {
                        ElMessage.success("查询成功") // 显示消息提醒
                        console.log(response.data.records)
                        this.queryVisible = false // 将对话框设置为不可见

                        let records = response.data.records // 接收响应负载
                        records.forEach(record => { // 对于每个借阅记录
                            this.queryResults.push(record) // 将其加入到查询结果列表中
                        });

                        this.ShowQueryResults = true // 显示查询结果

                        this.queryInfo.category = ''
                        this.queryInfo.title = ''
                        this.queryInfo.press = ''
                        this.queryInfo.minYear = ''
                        this.queryInfo.maxYear = ''
                        this.queryInfo.author = ''
                        this.queryInfo.minPrice = ''
                        this.queryInfo.maxPrice = ''
                    }
                });
        },
        ConfirmStore() {
            const publishY = this.storeBookInfo.publish_year.getFullYear().toString()
            axios.post("/book", {
                action: "store",
                book: {
                    bookId: '0',
                    category: this.storeBookInfo.category,
                    title: this.storeBookInfo.title,
                    press: this.storeBookInfo.press,
                    publishYear: publishY,
                    author: this.storeBookInfo.author,
                    price: this.storeBookInfo.price,
                    stock: this.storeBookInfo.stock
                }
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.storeBookVisible = false // 将对话框设置为不可见
                    this.storeBookInfo.category = ''
                    this.storeBookInfo.title = ''
                    this.storeBookInfo.press = ''
                    this.storeBookInfo.publish_year = ''
                    this.storeBookInfo.author = ''
                    this.storeBookInfo.price = ''
                    this.storeBookInfo.stock = ''
                }
            })
        },
        ConfirmBatchStore() {
            let book_list = []
            this.batchStoreBookInfo.forEach(book => {
                book_list.push({
                    bookId: '0',
                    category: book.category,
                    title: book.title,
                    press: book.press,
                    publishYear: book.publishYear,
                    author: book.author,
                    price: book.price,
                    stock: book.stock
                })
            })
            console.log(book_list)

            axios.post("/book", {
                action: "storemulti",
                books: book_list
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.batchStoreBookVisible = false // 将对话框设置为不可见
                    this.batchStoreBookInfo = []
                }
            })
        },
        ConfirmDelete() {
            axios.post("/book", {
                action: "remove",
                bookID: this.toDelete
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.deleteBookVisible = false // 将对话框设置为不可见
                    this.toDelete = ''
                }
            })
        },
        ConfirmModifyStock() {
            axios.post("/book", {
                action: "incstock",
                bookID: this.modifyStockData.bookId,
                amount: this.modifyStockData.delta
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.modifyStockVisible = false // 将对话框设置为不可见
                    this.modifyStockData.bookId = ''
                    this.modifyStockData.delta = ''
                }
            })
        },
        ConfirmModifyInfo() {
            axios.post("/book", {
                action: "modify",
                book: {
                    bookId: this.modifyInfoData.bookId,
                    category: this.modifyInfoData.category,
                    title: this.modifyInfoData.title,
                    press: this.modifyInfoData.press,
                    publishYear: this.modifyInfoData.publish_year.getFullYear().toString(),
                    author: this.modifyInfoData.author,
                    price: this.modifyInfoData.price
                }
            })
            .then (response => {
                if (response.data.error) {
                    ElMessage.error(response.data.error) // 显示错误消息
                } else {
                    ElMessage.success(response.data.success) // 显示消息提醒
                    this.modifyInfoVisible = false // 将对话框设置为不可见
                    this.modifyInfoData.bookId = ''
                    this.modifyInfoData.category = ''
                    this.modifyInfoData.title = ''
                    this.modifyInfoData.press = ''
                    this.modifyInfoData.publish_year = ''
                    this.modifyInfoData.author = ''
                    this.modifyInfoData.price = ''
                }
            })
        } 
    },
}

</script>


<style scoped>
.cardBox {
    height: 240px;
    width: 160px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
    text-align: center;
    margin-top: 35px;
    margin-left: 27.5px;
    margin-right: 10px;
    margin-bottom: 10px;
    padding: 7.5px;
    padding-right: 10px;
    padding-top: 15px;
}
</style>