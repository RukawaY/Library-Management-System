<!-- TODO: YOUR CODE HERE -->
<template>
    <el-scrollbar height="100%" style="width: 100%; height: 100%; ">
        <div style="margin-top: 20px; margin-left: 40px; font-size: 2em; font-weight: bold; color:black">图书管理</div>  
        <el-button type="primary" size="large" class="buttons" @click="ChangeQueryFormVisible()">查询图书</el-button>  
        <el-button type="primary" size="large" class="buttons" @click="ChangeBorrowFormVisible()" v-if="is_admin">借书</el-button>
        <el-button type="primary" size="large" class="buttons" @click="ChangeReturnFormVisible()" v-if="is_admin">还书</el-button>
        <el-button type="primary" size="large" class="buttons" @click="ChangeAdminButtonsVisible()" v-if="is_admin">管理图书</el-button>  
        
        <!-- manage book -->
        <br>
        <el-button type="info" size="large" class="buttons" v-show="admin_buttons_visible" v-if="is_admin" @click="ChangeStoreBookFormVisible()">图书入库</el-button>
        <el-button type="info" size="large" class="buttons" v-show="admin_buttons_visible" v-if="is_admin" @click="ChangeStoreMultipleBooksFormVisible()">图书批量入库</el-button>
        <el-button type="info" size="large" class="buttons" v-show="admin_buttons_visible" v-if="is_admin" @click="ChangeAddStockFormVisible()">图书增加库存</el-button>
        <el-button type="info" size="large" class="buttons" v-show="admin_buttons_visible" v-if="is_admin" @click="ChangeDeleteBookFormVisible()">图书删除</el-button>
        <el-button type="info" size="large" class="buttons" v-show="admin_buttons_visible" v-if="is_admin" @click="ChangeModifyBookFormVisible()">图书修改</el-button>
    
        <!-- borrow form-->
        <el-form ref="borrow_form" :model="borrow_form_model" label-position="top" class="form" v-if="borrow_form_visible">
            <el-form-item label="书号">
                <el-input v-model="borrow_form_model.borrower" class="input"></el-input>
            </el-form-item>
            <el-form-item label="卡号">
                <el-input v-model="borrow_form_model.card_number" class="input"></el-input>
            </el-form-item>
            <el-form-item label="借书时间">
                <el-input v-model="borrow_form_model.date" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitBorrowForm()" 
            :disabled="borrow_form_model.borrower.length === 0 || borrow_form_model.card_number.length === 0 || borrow_form_model.date.length === 0">借书</el-button>
            <el-button type="danger" class="buttons" @click="borrow_form_visible = false, ClearFormModelData(borrow_form_model)">取消</el-button>
        </el-form>

        <!-- return form-->
        <el-form ref="return_form" :model="return_form_model" label-position="top" class="form" v-if="return_form_visible">
            <el-form-item label="书号">
                <el-input v-model="return_form_model.returner" class="input"></el-input>
            </el-form-item>
            <el-form-item label="卡号">
                <el-input v-model="return_form_model.card_number" class="input"></el-input>
            </el-form-item>
            <el-form-item label="还书时间">
                <el-input v-model="return_form_model.date" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitReturnForm()"
            :disabled="return_form_model.returner.length === 0 || return_form_model.card_number.length === 0 || return_form_model.date.length === 0">还书</el-button>
            <el-button type="danger" class="buttons" @click="return_form_visible = false, ClearFormModelData(return_form_model)">取消</el-button>
        </el-form>

        <!-- query form-->
        <el-form ref="query_form" :model="query_form_model" label-position="top" class="form" v-if="query_form_visible">
            <el-form-item label="类别">
                <el-input v-model="query_form_model.category" class="input"></el-input>
            </el-form-item>
            <el-form-item label="书名">
                <el-input v-model="query_form_model.title" class="input"></el-input>
            </el-form-item>
            <el-form-item label="出版社">
                <el-input v-model="query_form_model.press" class="input"></el-input>
            </el-form-item>
            <el-form-item label="年份范围">
                <el-input v-model="query_form_model.year_begin" class="input" placeholder="起始年份" style="margin-bottom:10px;"></el-input>
            </el-form-item>
            <el-form-item>
                <el-input v-model="query_form_model.year_end" class="input" placeholder="终止年份"></el-input>
            </el-form-item>
            <el-form-item label="作者">
                <el-input v-model="query_form_model.author" class="input"></el-input>
            </el-form-item>
            <el-form-item label="价格范围">
                <el-input v-model="query_form_model.price_begin" class="input" placeholder="最低价格" style="margin-bottom:10px;"></el-input>
            </el-form-item>
            <el-form-item>
                <el-input v-model="query_form_model.price_end" class="input" placeholder="最高价格"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitQueryForm()"
            :disabled="query_form_model.category.length === 0 || query_form_model.title.length === 0 || query_form_model.press.length === 0 || query_form_model.year_begin.length === 0 || query_form_model.year_end.length === 0 || query_form_model.author.length === 0 || query_form_model.price_begin.length === 0 || query_form_model.price_end.length === 0">查询</el-button>
            <el-button type="danger" class="buttons" @click="query_form_visible = false, ClearFormModelData(query_form_model)">取消</el-button>
        </el-form>

        <!-- store book form -->
        <el-form ref="query_form" :model="query_form_model" label-position="top" class="form" v-if="store_book_form_visible">
            <el-form-item label="ID">
                <el-input v-model="query_form_model.book_id" class="input"></el-input>
            </el-form-item>
            <el-form-item label="类别">
                <el-input v-model="query_form_model.category" class="input"></el-input>
            </el-form-item>
            <el-form-item label="书名">
                <el-input v-model="query_form_model.title" class="input"></el-input>
            </el-form-item>
            <el-form-item label="出版社">
                <el-input v-model="query_form_model.press" class="input"></el-input>
            </el-form-item>
            <el-form-item label="出版年份">
                <el-input v-model="query_form_model.publish_year" class="input"></el-input>
            </el-form-item>
            <el-form-item label="作者">
                <el-input v-model="query_form_model.author" class="input"></el-input>
            </el-form-item>
            <el-form-item label="价格">
                <el-input v-model="query_form_model.price" class="input"></el-input>
            </el-form-item>
            <el-form-item label="库存量">
                <el-input v-model="query_form_model.stock" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitStoreBookForm()"
            :disabled="query_form_model.book_id.length === 0 || query_form_model.category.length === 0 || query_form_model.title.length === 0 || query_form_model.press.length === 0 || query_form_model.publish_year.length === 0 || query_form_model.author.length === 0 || query_form_model.price.length === 0 || query_form_model.stock.length === 0">入库</el-button>
            <el-button type="danger" class="buttons" @click="stock_book_form_visible = false, ClearFormModelData(query_form_model)">取消</el-button>
        </el-form>

        <div v-if="store_multiple_books_form_visible">
            <el-upload
                v-model:file-list="fileList"
                multiple
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                :limit="3"
                :on-exceed="handleExceed"
            >
            <el-button type="primary" class="buttons">点击上传txt文件（不超过3个）</el-button>
        </el-upload>
        </div>

        <!--add stock form-->
        <el-form ref="add_stock_form" :model="add_stock_form_model" label-position="top" class="form" v-if="add_stock_form_visible">
            <el-form-item label="书号">
                <el-input v-model="add_stock_form_model.book_id" class="input"></el-input>
            </el-form-item>
            <el-form-item label="变化量">
                <el-input v-model="add_stock_form_model.delta" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitAddStockForm()"
            :disabled="add_stock_form_model.book_id.length === 0 || add_stock_form_model.delta.length === 0">确定</el-button>
            <el-button type="danger" class="buttons" @click="add_stock_form_visible = false, ClearFormModelData(add_stock_form_model)">取消</el-button>
        </el-form>

        <!--delete book form-->
        <el-form ref="delete_book_form" :model="delete_book_form_model" label-position="top" class="form" v-if="delete_book_form_visible">
            <el-form-item label="书号">
                <el-input v-model="delete_book_form_model.book_id" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitDeleteBookForm()"
            :disabled="delete_book_form_model.book_id.length === 0">删除</el-button>
            <el-button type="danger" class="buttons" @click="delete_book_form_visible = false, ClearFormModelData(delete_book_form_model)">取消</el-button>
        </el-form>

        <!--modify book-->
        <el-form ref="modify_book_form" :model="modify_book_form_model" label-position="top" class="form" v-if="modify_book_form_visible">
            <el-form-item label="修改信息">
                <el-select
                    v-model="modify_book_form_model.value_to_modify"
                    placeholder="请选择要修改的图书信息"
                    size="large"
                    style="width: 240px"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="修改值">
                <el-input v-model="modify_book_form_model.new_value" class="input"></el-input>
            </el-form-item>
            <el-button type="primary" class="buttons" @click="submitModifyBookForm()"
            :disabled="modify_book_form_model.value_to_modify.length === 0 || modify_book_form_model.new_value.length === 0">修改</el-button>
            <el-button type="danger" class="buttons" @click="modify_book_form_visible = false, ClearFormModelData(modify_book_form_model)">取消</el-button>
        </el-form>

    </el-scrollbar>
</template>

<script>
export default {
    data() {
        return {
            is_admin: true,
            borrow_form_visible: false,
            return_form_visible: false,
            query_form_visible: false,
            admin_buttons_visible: false,
            store_book_form_visible: false,
            store_multiple_books_form_visible: false,
            add_stock_form_visible: false,
            delete_book_form_visible: false,
            modify_book_form_visible: false,
            borrow_form_model: {
                borrower: '',
                card_number: '',
                date: ''
            },
            return_form_model: {
                returner: '',
                card_number: '',
                date: ''
            },
            query_form_model: {
                category: '',
                title: '',
                press: '',
                year_begin: '',
                year_end: '',
                author: '',
                price_begin: '',
                price_end: ''
            },
            store_book_form_model: {
                book_id: '',
                category: '',
                title: '',
                press: '',
                publish_year: '',
                author: '',
                price: '',
                stock: ''
            },
            add_stock_form_model: {
                book_id: '',
                delta: ''
            },
            delete_book_form_model: {
                book_id: ''
            },
            modify_book_form_model: {
                value_to_modify: '',
                new_value: ''
            },
            options: [
                { value: 'category', label: '类别' },
                { value: 'title', label: '书名' },
                { value: 'press', label: '出版社' },
                { value: 'publish_year', label: '出版年份' },
                { value: 'author', label: '作者' },
                { value: 'price', label: '价格' },
            ],
        }
    },
    methods: {
        SetCurrentVisibleAndOthersInvisible(current) {
            this.borrow_form_visible = false;
            this.return_form_visible = false;
            this.query_form_visible = false;
            this.store_book_form_visible = false;
            this.store_multiple_books_form_visible = false;
            this.add_stock_form_visible = false;
            this.delete_book_form_visible = false;
            this.modify_book_form_visible = false;
            if (current === 'borrow_form_visible' || current === 'return_form_visible' || current === 'query_form_visible') {
                this.admin_buttons_visible = false;
            }

            if (current) {
                this[current] = true;
            }
        },
        ChangeBorrowFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('borrow_form_visible');
        },
        ChangeReturnFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('return_form_visible');
        },
        ChangeQueryFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('query_form_visible');
        },
        ChangeStoreBookFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('store_book_form_visible');
        },
        ChangeStoreMultipleBooksFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('store_multiple_books_form_visible');
        },
        ChangeAddStockFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('add_stock_form_visible');
        },
        ChangeDeleteBookFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('delete_book_form_visible');
        },
        ChangeModifyBookFormVisible() {
            this.SetCurrentVisibleAndOthersInvisible('modify_book_form_visible');
        },
        ClearFormModelData (model) {
            for (let key in model) {
                model[key] = '';
            }
        },
        ChangeAdminButtonsVisible() {
            if (this.admin_buttons_visible) {
                this.admin_buttons_visible = false;
            } else {
                this.admin_buttons_visible = true;
            }
        },
        // 借书表单提交
        submitBorrowForm() {
            console.log('借书表单数据:', this.borrow_form_model);
            // 调用 API 或处理数据
            this.ClearFormModelData(this.borrow_form_model);
            this.borrow_form_visible = false; // 隐藏表单
        },
        // 还书表单提交
        submitReturnForm() {
            // 调用 API 或处理数据
            console.log('还书表单数据:', this.return_form_model);
            this.ClearFormModelData(this.return_form_model);
            this.return_form_visible = false; // 隐藏表单
        },
        // 查询表单提交
        submitQueryForm() {
            console.log('查询表单数据:', this.query_form_model);
            // 调用 API 或处理数据
            this.ClearFormModelData(this.query_form_model);
            this.query_form_visible = false; // 隐藏表单
        },
        // 图书入库表单提交
        submitStoreBookForm() {
            console.log('图书入库表单数据:', this.store_book_form_model);
            // 调用 API 或处理数据
            this.ClearFormModelData(this.store_book_form_model);
            this.store_book_form_visible = false; // 隐藏表单
        },
        // 增加库存表单提交
        submitAddStockForm() {
            console.log('增加库存表单数据:', this.add_stock_form_model);
            // 调用 API 或处理数据
            this.ClearFormModelData(this.add_stock_form_model);
            this.add_stock_form_visible = false; // 隐藏表单
        },
        // 删除图书表单提交
        submitDeleteBookForm() {
            console.log('删除图书表单数据:', this.delete_book_form_model);
            this.ClearFormModelData(this.delete_book_form_model);
            this.delete_book_form_visible = false; // 隐藏表单
        },
        // 修改图书表单提交
        submitModifyBookForm() {
            console.log('修改图书表单数据:', this.modify_book_form_model);
            // 调用 API 或处理数据
            this.ClearFormModelData(this.modify_book_form_model);
            this.modify_book_form_visible = false; // 隐藏表单
        }           
    }
}
</script>

<style>
.input {
    width: 500px !important;
}
.buttons {
    margin-top: 15px;
    margin-left: 20px;
    margin-bottom: 15px;
    margin-right: 20px;
}
.form {
    margin-top: 40px; 
    margin-left: 40px; 
    margin-right: 40px;
}
</style>