### 下面是将机器人设置为群管理员
```json
{
  "update_id": 187326718,
  "my_chat_member": {
    "chat": {
      "id": -1001914618483,
      "title": "ai测试",
      "type": "supergroup"
    },
    "from": {
      "id": 5519214459,
      "is_bot": false,
      "first_name": "hong",
      "last_name": "bing",
      "language_code": "zh-hans"
    },
    "date": 1685449242,
    "old_chat_member": {
      "user": {
        "id": 6067617432,
        "is_bot": true,
        "first_name": "智能AI语言模型",
        "username": "ai999_bot"
      },
      "status": "member"
    },
    "new_chat_member": {
      "user": {
        "id": 6067617432,
        "is_bot": true,
        "first_name": "智能AI语言模型",
        "username": "ai999_bot"
      },
      "status": "administrator",
      "can_be_edited": false,
      "can_manage_chat": true,
      "can_change_info": true,
      "can_delete_messages": true,
      "can_invite_users": true,
      "can_restrict_members": true,
      "can_pin_messages": true,
      "can_manage_topics": false,
      "can_promote_members": false,
      "can_manage_video_chats": true,
      "is_anonymous": false,
      "can_manage_voice_chats": true
    }
  }
}
```
### 下面是群消息
```json
{
  "update_id": 187326719,
  "message": {
    "message_id": 10,
    "from": {
      "id": 5519214459,
      "is_bot": false,
      "first_name": "hong",
      "last_name": "bing",
      "language_code": "zh-hans"
    },
    "chat": {
      "id": -1001914618483,
      "title": "ai测试",
      "type": "supergroup"
    },
    "date": 1685449265,
    "text": "@ai999_bot 666",
    "entities": [
      {
        "offset": 0,
        "length": 10,
        "type": "mention"
      }
    ]
  }
}
```
### 下面是单人消息
```json
{
  "update_id": 187326717,
  "message": {
    "message_id": 33,
    "from": {
      "id": 5519214459,
      "is_bot": false,
      "first_name": "hong",
      "last_name": "bing",
      "language_code": "zh-hans"
    },
    "chat": {
      "id": 5519214459,
      "first_name": "hong",
      "last_name": "bing",
      "type": "private"
    },
    "date": 1685449163,
    "text": "777"
  }
}
```