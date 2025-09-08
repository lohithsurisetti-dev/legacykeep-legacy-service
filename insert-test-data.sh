#!/bin/bash

# =============================================================================
# Legacy Service - Test Data Insertion Script
# =============================================================================

BASE_URL="http://localhost:8085/api/v1"
echo "🚀 Starting Legacy Service Test Data Insertion..."

# Test data variables
FAMILY_ID="550e8400-e29b-41d4-a716-446655440000"
CREATOR_ID="550e8400-e29b-41d4-a716-446655440100"
SON_ID="550e8400-e29b-41d4-a716-446655440101"
DAUGHTER_ID="550e8400-e29b-41d4-a716-446655440102"

echo ""
echo "📋 Step 1: Creating Test Buckets..."

# Create Family Stories Bucket
echo "Creating 'Family Stories' bucket..."
BUCKET1_RESPONSE=$(curl -s -X POST "$BASE_URL/buckets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Family Stories",
    "description": "Collection of family stories and memories",
    "categoryId": "550e8400-e29b-41d4-a716-446655440012",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "privacyLevel": "FAMILY",
    "isActive": true
  }')

echo "Bucket 1 Response: $BUCKET1_RESPONSE"
BUCKET1_ID=$(echo $BUCKET1_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Bucket 1 ID: $BUCKET1_ID"

# Create Life Lessons Bucket
echo "Creating 'Life Lessons' bucket..."
BUCKET2_RESPONSE=$(curl -s -X POST "$BASE_URL/buckets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Life Lessons",
    "description": "Wisdom and life experiences to pass down",
    "categoryId": "550e8400-e29b-41d4-a716-446655440014",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "privacyLevel": "FAMILY",
    "isActive": true
  }')

echo "Bucket 2 Response: $BUCKET2_RESPONSE"
BUCKET2_ID=$(echo $BUCKET2_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Bucket 2 ID: $BUCKET2_ID"

# Create Family Recipes Bucket
echo "Creating 'Family Recipes' bucket..."
BUCKET3_RESPONSE=$(curl -s -X POST "$BASE_URL/buckets" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Family Recipes",
    "description": "Traditional family recipes and cooking secrets",
    "categoryId": "550e8400-e29b-41d4-a716-446655440031",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "privacyLevel": "FAMILY",
    "isActive": true
  }')

echo "Bucket 3 Response: $BUCKET3_RESPONSE"
BUCKET3_ID=$(echo $BUCKET3_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Bucket 3 ID: $BUCKET3_ID"

echo ""
echo "📝 Step 2: Creating Test Content..."

# Create Family Story Content
echo "Creating 'Grandfather's War Story' content..."
CONTENT1_RESPONSE=$(curl -s -X POST "$BASE_URL/content" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Grandfather'\''s War Story",
    "content": "When I was 25, I served in the army during the war. It was a difficult time, but it taught me the value of courage, friendship, and never giving up. I want you both to know that no matter what challenges you face in life, you have the strength within you to overcome them. Your grandfather was a brave man, and I see that same courage in both of you.",
    "contentType": "TEXT",
    "bucketId": "'$BUCKET1_ID'",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "generationLevel": 1,
    "privacyLevel": "FAMILY",
    "isFeatured": true,
    "sortOrder": 1,
    "mediaFiles": [
      {
        "fileName": "grandfather_war_photo.jpg",
        "originalFileName": "grandfather_war_photo.jpg",
        "fileSize": 2048576,
        "mimeType": "image/jpeg",
        "s3Url": "https://legacykeep-bucket.s3.amazonaws.com/photos/grandfather_war_photo.jpg",
        "thumbnailUrl": "https://legacykeep-bucket.s3.amazonaws.com/thumbnails/grandfather_war_photo_thumb.jpg",
        "fileType": "IMAGE"
      }
    ],
    "recipients": [
      {
        "recipientId": "'$SON_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "son",
        "accessLevel": "READ",
        "personalMessage": "This story is especially for you, my son. I hope it inspires you to be brave in your own life."
      },
      {
        "recipientId": "'$DAUGHTER_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "daughter",
        "accessLevel": "READ",
        "personalMessage": "My dear daughter, this story shows that courage comes in many forms. You have that same strength."
      }
    ]
  }')

echo "Content 1 Response: $CONTENT1_RESPONSE"
CONTENT1_ID=$(echo $CONTENT1_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Content 1 ID: $CONTENT1_ID"

# Create Life Lesson Content
echo "Creating 'Value of Education' content..."
CONTENT2_RESPONSE=$(curl -s -X POST "$BASE_URL/content" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Value of Education",
    "content": "Education is the most powerful tool you can have. It opens doors, broadens your perspective, and gives you the ability to make informed decisions. I never had the opportunity to go to college, but I worked hard to educate myself through books and experience. I want you both to pursue your education with passion and never stop learning, even after you finish school.",
    "contentType": "TEXT",
    "bucketId": "'$BUCKET2_ID'",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "generationLevel": 1,
    "privacyLevel": "FAMILY",
    "isFeatured": true,
    "sortOrder": 1,
    "recipients": [
      {
        "recipientId": "'$SON_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "son",
        "accessLevel": "READ",
        "personalMessage": "Son, education will be your foundation for success. Study hard and never give up on your dreams."
      },
      {
        "recipientId": "'$DAUGHTER_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "daughter",
        "accessLevel": "READ",
        "personalMessage": "Daughter, knowledge is power. Use your education to make a difference in the world."
      }
    ]
  }')

echo "Content 2 Response: $CONTENT2_RESPONSE"
CONTENT2_ID=$(echo $CONTENT2_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Content 2 ID: $CONTENT2_ID"

# Create Recipe Content
echo "Creating 'Grandmother'\''s Secret Recipe' content..."
CONTENT3_RESPONSE=$(curl -s -X POST "$BASE_URL/content" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Grandmother'\''s Secret Biryani Recipe",
    "content": "This is the secret recipe that has been passed down in our family for generations. The key is in the spices and the slow cooking process. Ingredients: 2 cups basmati rice, 1 kg chicken, 3 onions, 2 tomatoes, ginger-garlic paste, yogurt, and the secret spice mix. Method: Marinate chicken for 2 hours, cook rice 70%, layer with chicken and cook on dum for 30 minutes. The secret is in the timing and the love you put into it.",
    "contentType": "TEXT",
    "bucketId": "'$BUCKET3_ID'",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "generationLevel": 1,
    "privacyLevel": "FAMILY",
    "isFeatured": true,
    "sortOrder": 1,
    "mediaFiles": [
      {
        "fileName": "biryani_recipe_video.mp4",
        "originalFileName": "biryani_recipe_video.mp4",
        "fileSize": 52428800,
        "mimeType": "video/mp4",
        "s3Url": "https://legacykeep-bucket.s3.amazonaws.com/videos/biryani_recipe_video.mp4",
        "thumbnailUrl": "https://legacykeep-bucket.s3.amazonaws.com/thumbnails/biryani_recipe_thumb.jpg",
        "fileType": "VIDEO"
      },
      {
        "fileName": "spice_mix_photo.jpg",
        "originalFileName": "spice_mix_photo.jpg",
        "fileSize": 1536000,
        "mimeType": "image/jpeg",
        "s3Url": "https://legacykeep-bucket.s3.amazonaws.com/photos/spice_mix_photo.jpg",
        "thumbnailUrl": "https://legacykeep-bucket.s3.amazonaws.com/thumbnails/spice_mix_thumb.jpg",
        "fileType": "IMAGE"
      }
    ],
    "recipients": [
      {
        "recipientId": "'$SON_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "son",
        "accessLevel": "READ",
        "personalMessage": "Son, this recipe is our family treasure. Learn it well and pass it on to your children."
      },
      {
        "recipientId": "'$DAUGHTER_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "daughter",
        "accessLevel": "READ",
        "personalMessage": "Daughter, cooking is an art. This recipe will help you create memories for your own family."
      }
    ]
  }')

echo "Content 3 Response: $CONTENT3_RESPONSE"
CONTENT3_ID=$(echo $CONTENT3_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Content 3 ID: $CONTENT3_ID"

# Create Audio Message Content
echo "Creating 'Voice Message for Children' content..."
CONTENT4_RESPONSE=$(curl -s -X POST "$BASE_URL/content" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Voice Message for You",
    "content": "This is a special voice message I recorded for you both. Sometimes hearing a loved one'\''s voice can bring more comfort than reading words.",
    "contentType": "AUDIO",
    "bucketId": "'$BUCKET1_ID'",
    "creatorId": "'$CREATOR_ID'",
    "familyId": "'$FAMILY_ID'",
    "generationLevel": 1,
    "privacyLevel": "FAMILY",
    "isFeatured": false,
    "sortOrder": 2,
    "mediaFiles": [
      {
        "fileName": "voice_message_family.mp3",
        "originalFileName": "voice_message_family.mp3",
        "fileSize": 8388608,
        "mimeType": "audio/mpeg",
        "s3Url": "https://legacykeep-bucket.s3.amazonaws.com/audio/voice_message_family.mp3",
        "fileType": "AUDIO"
      }
    ],
    "recipients": [
      {
        "recipientId": "'$SON_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "son",
        "accessLevel": "READ",
        "personalMessage": "Son, listen to this when you need to hear my voice."
      },
      {
        "recipientId": "'$DAUGHTER_ID'",
        "recipientType": "INDIVIDUAL",
        "recipientRelationship": "daughter",
        "accessLevel": "READ",
        "personalMessage": "Daughter, this is my voice saying I love you both very much."
      }
    ]
  }')

echo "Content 4 Response: $CONTENT4_RESPONSE"
CONTENT4_ID=$(echo $CONTENT4_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Content 4 ID: $CONTENT4_ID"

echo ""
echo "✅ Test Data Insertion Complete!"
echo ""
echo "📊 Summary:"
echo "  - Created 3 buckets: Family Stories, Life Lessons, Family Recipes"
echo "  - Created 4 content items with media files and recipients"
echo "  - All content is targeted to both son and daughter"
echo "  - Mix of text, audio, and video content types"
echo ""
echo "🔍 You can now test the following APIs:"
echo "  - GET /api/v1/buckets - List all buckets"
echo "  - GET /api/v1/content - List all content"
echo "  - GET /api/v1/content/search?query=grandfather - Search content"
echo "  - GET /api/v1/content/featured - Get featured content"
echo "  - GET /api/v1/content/bucket/{bucketId} - Get content by bucket"
echo ""
echo "🎯 Test data includes:"
echo "  - Family stories with photos"
echo "  - Life lessons and wisdom"
echo "  - Traditional recipes with videos"
echo "  - Voice messages for children"
echo "  - Targeted delivery to specific recipients"
