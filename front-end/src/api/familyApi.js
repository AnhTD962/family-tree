import axios from './axios'

export const getFamilyTree = () => axios.get('/family-members/tree').then((res) => res.data)

export const getMember = (id) => axios.get(`/family-members/${id}`).then((res) => res.data)

export const search = (query) =>
  axios.get(`/family-members/search?query=${query}`).then((res) => res.data)

export const createMember = (member) =>
  axios.post('/family-members', member).then((res) => res.data)

export const updateMember = (id, member) =>
  axios.put(`/family-members/${id}`, member).then((res) => res.data)

export const deleteMember = (id) => axios.delete(`/family-members/${id}`)
