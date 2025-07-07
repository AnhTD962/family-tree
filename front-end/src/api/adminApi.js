import axios from './axios'

export const getUsers = () => axios.get('/admin/users').then((res) => res.data)

export const toggleUserStatus = (id) => axios.put(`/admin/users/${id}/toggle-status`)

export const getHistory = (page = 0, size = 20) =>
  axios.get(`/admin/history?page=${page}&size=${size}`).then((res) => res.data)
