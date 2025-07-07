export function buildTree(members) {
  const map = new Map()
  members.forEach((m) => map.set(m.id, { ...m, children: [] }))

  let root = null

  members.forEach((m) => {
    const node = map.get(m.id)
    const parent = m.fatherId ? map.get(m.fatherId) : m.motherId ? map.get(m.motherId) : null
    if (parent) {
      parent.children.push(node)
    } else if (!root) {
      root = node
    }
  })

  const convert = (member) => ({
    name: member.fullName,
    gender: member.gender,
    children: member.children.map(convert),
  })

  return convert(root)
}
