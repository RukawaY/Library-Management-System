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
                    <div style="font-size: 25px; font-weight: bold;">我要借书</div>
                    <el-divider />
                    <el-icon size="130px" color="rgb(121.3, 187.1, 255)" style="margin-top: 0.2rem;"><Notebook /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="newReturnVisible=true">
                <!-- 还书卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">我要还书</div>
                    <el-divider />
                    <el-icon size="130px" color="rgb(148.6, 212.3, 117.1)" style="margin-top: 0.2rem;"><DocumentChecked /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="queryVisible=true">
                <!-- 查询图书卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">查询图书</div>
                    <el-divider />
                    <el-icon size="130px" color="#606266" style="margin-top: 0.2rem;"><Search /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="storeBookVisible=true">
                <!-- 图书入库卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">图书入库</div>
                    <el-divider />
                    <el-icon size="130px" color="pink" style="margin-top: 0.2rem;"><Collection /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="batchStoreBookVisible=true">
                <!-- 图书批量入库卡片 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">图书批量入库</div>
                    <el-divider />
                    <el-icon size="130px" color="rgb(237.5, 189.9, 118.5)" style="margin-top: 0.2rem;"><Management /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox" @click="deleteBookVisible=true">
                <!-- 删除图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">删除图书</div>
                    <el-divider />
                    <el-icon size="130px" color="rgb(196, 86.4, 86.4)" style="margin-top: 0.2rem;"><FolderDelete /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox">
                <!-- 修改库存图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">图书库存修改</div>
                    <el-divider />
                    <el-icon size="130px" color="teal" style="margin-top: 0.2rem;"><EditPen /></el-icon>
                </div>
            </el-button>

            <el-button class="cardBox">
                <!-- 修改信息图书 -->
                <div>
                    <!-- 卡片标题 -->
                    <div style="font-size: 25px; font-weight: bold;">图书信息修改</div>
                    <el-divider />
                    <el-icon size="130px" color="black" style="margin-top: 0.2rem;"><InfoFilled /></el-icon>
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
                    <el-button type="primary" @click="ConfirmNewBorrow, newBorrowVisible = false"
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
                    <el-button type="primary" @click="ConfirmNewReturn, newReturnVisible = false"
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
                    <el-button type="primary" @click="ConfirmQuery, queryVisible = false"
                        :disabled="queryInfo.category.length === 0 || queryInfo.title.length === 0 || queryInfo.press.length === 0 || queryInfo.minYear.length === 0 || queryInfo.maxYear.length === 0 || queryInfo.author.length === 0 || queryInfo.minPrice.length === 0 || queryInfo.maxPrice.length === 0">确定</el-button>
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
                    <el-button type="primary" @click="ConfirmStore, storeBookVisible = false"
                        :disabled="storeBookInfo.category.length === 0 || storeBookInfo.title.length === 0 || storeBookInfo.press.length === 0 || storeBookInfo.publish_year.length === 0 || storeBookInfo.author.length === 0 || storeBookInfo.price.length === 0 || storeBookInfo.stock.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 批量入库对话框 -->
        <el-dialog v-model="batchStoreBookVisible" title="图书批量入库" width="30%" align-center>
            <div style="margin-left: 2vw; font-weight: bold; font-size: 1rem; margin-top: 20px; ">
                请上传包含图书信息的txt文件：
                <el-upload
                    v-model:file-list="filelist"
                    class="upload-demo"
                    multiple
                    :on-remove="handleRemove"
                    :on-preview="handlePreview"
                    :on-exceed="handleExceed"
                    :before-remove="beforeRemove"
                    :limit="3"
                >
                    <el-button type="primary">点击上传文件</el-button>
                    <template #tip>
                    <div class="el-upload__tip">
                        文件数量限制：3
                    </div>
                    </template>
                </el-upload>
            </div>

            <template #footer>
                <span>
                    <el-button type="danger" @click="batchStoreBookVisible = false">取消</el-button>
                    <el-button type="primary" @click="ConfirmBatchStore, batchStoreBookVisible = false"
                        :disabled="filelist.length === 0">确定</el-button>
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
                    <el-button type="primary" @click="ConfirmDelete, deleteBookVisible = false"
                        :disabled="toDelete.length === 0">确定</el-button>
                </span>
            </template>
        </el-dialog>

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
            filelist: [], // 批量入库文件列表
            deleteBookVisible: false, // 删除图书对话框可见性
            toDelete: '',
            
            
            
            
            
            cards: [{ // 借书证列表
                id: 1,
                name: '小明',
                department: '计算机学院',
                type: '学生'
            }, {
                id: 2,
                name: '王老师',
                department: '计算机学院',
                type: '教师'
            }
            ],
            Delete,
            Edit,
            Search,
            action: '', // 借书证操作类型
            toSearch: '', // 搜索内容
            types: [ // 借书证类型
                {
                    value: '教师',
                    label: '教师',
                },
                {
                    value: '学生',
                    label: '学生',
                }
            ],
            removeCardVisible: false, // 删除借书证对话框可见性
            toRemove: 0, // 待删除借书证号
            newCardInfo: { // 待新建借书证信息
                name: '',
                department: '',
                type: '学生'
            },
            modifyCardVisible: false, // 修改信息对话框可见性
            toModifyInfo: { // 待修改借书证信息
                id: 0,
                name: '',
                department: '',
                type: '学生'
            },
        }
    },
    methods: {
        handleExceed(files, uploadFiles) {
            ElMessage.warning(
                `The limit is 3, you selected ${files.length} files this time, add up to ${
                files.length + uploadFiles.length
                } totally`
            );
        },
        beforeRemove(uploadFile, uploadFiles) {
            return ElMessageBox.confirm(
                `确定删除文件${uploadFile.name}吗?`
            ).then(
                () => true,
                () => false
            );
        },






        ConfirmNewCard() {
            let tempType = '';
            if (this.newCardInfo.type === '教师') {
                tempType = 'T'
            } else {
                tempType = 'S'
            }
            axios.post("/card",
                { // 请求体
                    action: "register",
                    card: {
                        cardId: 0,
                        name: this.newCardInfo.name,
                        department: this.newCardInfo.department,
                        type: tempType
                    }
                })
                .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error) // 显示错误消息
                    } else {
                        ElMessage.success("借书证新建成功") // 显示消息提醒
                        this.newCardVisible = false // 将对话框设置为不可见
                        this.QueryCards() // 重新查询借书证以刷新页面
                    }
                })
        },
        ConfirmModifyCard() {
            let tempType = '';
            if (this.toModifyInfo.type === '教师') {
                tempType = 'T'
            } else {
                tempType = 'S'
            }
            axios.post("/card",
                {
                    action: "modify",
                    card: {
                        cardId: this.toModifyInfo.id,
                        name: this.toModifyInfo.name,
                        department: this.toModifyInfo.department,
                        type: tempType
                    }
                })
                .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error) // 显示错误消息
                    } else {
                        ElMessage.success("借书证信息修改成功")
                        this.modifyCardVisible = false
                        this.QueryCards()
                    }
                })          
        },
        ConfirmRemoveCard() {
            axios.post("/card",
                {
                    action: "remove",
                    cardId: this.toRemove
                })
                .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error) // 显示错误消息
                    } else {
                        ElMessage.success("借书证删除成功")
                        this.removeCardVisible = false
                        this.QueryCards()
                    }
                })
        },
        QueryCards() {
            this.cards = [] // 清空列表
            let response = axios.get('/card?type=records') // 向/card发出GET请求
                .then(response => {
                    if (response.data.error) {
                        ElMessage.error(response.data.error) // 显示错误消息
                    } else {
                        let cards = response.data.records // 接收响应负载
                        console.log(response.data)
                        cards.forEach(card => { // 对于每个借书证
                            this.cards.push(card) // 将其加入到列表中
                        })
                    }
                })
        }
    },
    mounted() { // 当页面被渲染时
        this.QueryCards() // 查询借书证
    }
}

</script>


<style scoped>
.cardBox {
    height: 300px;
    width: 200px;
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

.newCardBox {
    height: 300px;
    width: 200px;
    margin-top: 40px;
    margin-left: 27.5px;
    margin-right: 10px;
    padding: 7.5px;
    padding-right: 10px;
    padding-top: 15px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
    text-align: center;
}
</style>