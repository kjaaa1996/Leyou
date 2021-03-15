<template>
  <v-card>
    <v-flex xs12 sm10>
      <v-tree url="/item/category/list"
              :isEdit="isEdit"
              @handleAdd="handleAdd"
              @handleEdit="handleEdit"
              @handleDelete="handleDelete"
              @handleClick="handleClick"
      />
    </v-flex>
  </v-card>
</template>

<script>
  export default {
    name: "category",
    data() {
      return {
        isEdit: true
      }
    },
    methods: {
      handleAdd(node) {
        this.$http.post("/item/category?" + this.$qs.stringify(node))
          .then(() => {
            this.$message.info("添加成功!刷新后生效");
            location.reload();
          })
          .catch(() => this.$message.info("添加失败!"));
      },
      handleEdit(id, name) {
        //在TreeItem中有修改后的操作函数，不需要在这里监听node的改变
        const node = {
          id: id,
          name: name
        };
        this.$http.put("/item/category?" + this.$qs.stringify(node))
          .then(() => this.$message.info("修改成功!"))
          .catch(() => this.$message.info("修改失败!"));
      },
      handleDelete(cid) {
        this.$http.delete("/item/category/delete/" + cid)
          .then(() => this.$message.info("删除成功!"))
          .catch(() => this.$message.info("删除失败!"));
      },
      handleClick(node) {
        console.log(node)
      }
    }
  };
</script>

<style scoped>

</style>
