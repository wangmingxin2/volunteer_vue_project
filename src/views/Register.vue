<template>
  <div class="register">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <input type="text" v-model="username" placeholder="用户名" />
      </div>
      <div class="form-group">
        <input type="password" v-model="password" placeholder="密码" />
      </div>
      <div class="form-group">
        <select v-model="role">
          <option value="">请选择角色</option>
          <option value="volunteer">志愿者</option>
          <option value="organization">志愿组织</option>
        </select>
      </div>
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const username = ref('')
const password = ref('')
const role = ref('')

const handleRegister = () => {
  // 处理注册逻辑
  console.log('注册')
}
</script>

<style scoped>
.register {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
}

.form-group {
  margin-bottom: 1rem;
}

input,
select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
}

button {
  width: 100%;
  padding: 0.5rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
