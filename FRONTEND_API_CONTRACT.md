# Frontend API Contract

Base URL:

```text
http://localhost:8080
```

Protected endpoints require:

```http
Authorization: Bearer {jwt}
```

Dev seed users, available when backend runs with `dev` profile:

```text
admin@fraktal.pl / Admin123!
student@fraktal.pl / Student123!
```

## 1. Auth

### Register

```http
POST /api/auth/register
```

Request:

```json
{
  "firstName": "Jan",
  "lastName": "Kowalski",
  "nickname": "janek",
  "email": "jan@example.com",
  "password": "Password123!"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

### Login

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "student@fraktal.pl",
  "password": "Student123!"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

### Current User

```http
GET /api/me
```

Use this after app reload to restore the session and user role.

Response shape:

```json
{
  "id": "...",
  "firstName": "Student",
  "lastName": "Testowy",
  "nickname": "student",
  "email": "student@fraktal.pl",
  "role": "ROLE_STUDENT"
}
```

## 2. Course Catalog

### Course Cards

```http
GET /api/courses/catalog
```

Use this for the course catalog/home page cards.

Response:

```json
[
  {
    "id": "...",
    "title": "Matura podstawowa - kurs testowy",
    "description": "...",
    "category": "Matura podstawowa",
    "thumbnailUrl": "https://...",
    "price": 199.0,
    "hasAccess": true,
    "lessonsCount": 24,
    "completedLessons": 6,
    "progressPercent": 25.0,
    "freePreviewAvailable": true,
    "canStart": true
  }
]
```

### Basic Published Courses

```http
GET /api/courses
```

Older/basic list endpoint. Prefer `/api/courses/catalog` for frontend cards.

## 3. Course Detail

### Course Details

```http
GET /api/courses/{courseId}
```

Returns basic course data.

### Course Access

```http
GET /api/courses/{courseId}/access
```

Use this on the course detail page to decide which CTA to render.

Response:

```json
{
  "courseId": "...",
  "published": true,
  "hasAccess": true,
  "admin": false,
  "canViewContent": true,
  "canStart": true,
  "freePreviewAvailable": true
}
```

Suggested frontend CTA logic:

```text
hasAccess=true       -> "Kontynuuj" or "Rozpocznij"
hasAccess=false
  and canStart=true  -> "Obejrzyj darmową lekcję"
hasAccess=false      -> "Kup kurs"
admin=true           -> "Podgląd kursu"
```

### Course Content / Sidebar

```http
GET /api/courses/{courseId}/content
```

Use this for the course sidebar and curriculum preview.

Response shape:

```json
{
  "id": "...",
  "title": "Matura podstawowa - kurs testowy",
  "description": "...",
  "category": "Matura podstawowa",
  "thumbnailUrl": "https://...",
  "price": 199.0,
  "published": true,
  "hasAccess": true,
  "chapters": [
    {
      "id": "...",
      "title": "Funkcje",
      "position": 0,
      "lessons": [
        {
          "id": "...",
          "title": "Funkcja liniowa",
          "description": "...",
          "position": 1,
          "free": false,
          "locked": false,
          "completed": true,
          "durationMinutes": 32
        }
      ]
    }
  ]
}
```

## 4. Start And Player

### Start Course

```http
GET /api/courses/{courseId}/start
```

Use this when user clicks `Rozpocznij` or `Kontynuuj`.

Response:

```json
{
  "courseId": "...",
  "chapterId": "...",
  "chapterTitle": "Funkcje",
  "lessonId": "...",
  "lessonTitle": "Wprowadzenie do funkcji",
  "mode": "START"
}
```

`mode` can be:

```text
START
CONTINUE
```

Frontend should navigate to the lesson player using `lessonId`.

### Continue Course

```http
GET /api/courses/{courseId}/continue
```

Alternative endpoint if the UI wants information about locked/completed state.

Response:

```json
{
  "courseId": "...",
  "chapterId": "...",
  "chapterTitle": "Funkcje",
  "lessonId": "...",
  "lessonTitle": "Funkcja liniowa",
  "lessonPosition": 1,
  "free": false,
  "locked": false,
  "courseCompleted": false
}
```

### Lesson Player

```http
GET /api/lessons/{lessonId}/play
```

Use this as the main player screen endpoint.

Response:

```json
{
  "lessonId": "...",
  "title": "Funkcja liniowa",
  "description": "...",
  "videoUrl": "https://...",
  "pdfUrl": "https://...",
  "durationMinutes": 32,
  "free": false,
  "chapterId": "...",
  "courseId": "...",
  "previousLessonId": "...",
  "nextLessonId": "...",
  "completed": false,
  "completedAt": null,
  "primaryVideo": {
    "id": "...",
    "lessonId": "...",
    "title": "Wideo lekcji",
    "type": "VIDEO",
    "url": "https://...",
    "provider": "EXTERNAL_URL",
    "providerAssetId": null,
    "durationSeconds": 1920,
    "thumbnailUrl": "https://...",
    "status": "READY",
    "position": 0
  },
  "primaryPdf": {
    "id": "...",
    "lessonId": "...",
    "title": "Notatka PDF",
    "type": "PDF",
    "url": "https://...",
    "provider": "EXTERNAL_URL",
    "providerAssetId": null,
    "durationSeconds": null,
    "thumbnailUrl": null,
    "status": "READY",
    "position": 1
  },
  "materials": [
    {
      "id": "...",
      "lessonId": "...",
      "title": "Notatka PDF",
      "type": "PDF",
      "url": "https://...",
      "provider": "EXTERNAL_URL",
      "providerAssetId": null,
      "durationSeconds": null,
      "thumbnailUrl": null,
      "status": "READY",
      "position": 0
    }
  ]
}
```

`primaryVideo` is the first `VIDEO` material ordered by position.
`primaryPdf` is the first `PDF` material ordered by position.
Both fields can be `null` when the lesson does not have that material yet.

### Lesson Media Rules

For new frontend screens:

```text
Use primaryVideo instead of videoUrl.
Use primaryPdf instead of pdfUrl.
Use /api/admin/lessons/{lessonId}/video to manage the main lesson video.
Use /api/admin/lessons/{lessonId}/pdf to manage the main lesson PDF.
Use /api/admin/lessons/{lessonId}/materials for additional materials only.
Treat lesson.videoUrl and lesson.pdfUrl as backward compatibility fields.
```

### Lesson Materials Only

```http
GET /api/lessons/{lessonId}/materials
```

Usually not needed on first player load because `/play` already includes `materials`.
Useful for refreshing materials separately.

## 5. Progress

### Complete Lesson

```http
POST /api/lessons/{lessonId}/complete
```

Response:

```json
{
  "lessonId": "...",
  "completed": true,
  "completedAt": "2026-07-14T12:00:00"
}
```

### Uncomplete Lesson

```http
DELETE /api/lessons/{lessonId}/complete
```

Response:

```json
{
  "lessonId": "...",
  "completed": false,
  "completedAt": null
}
```

### Course Progress

```http
GET /api/courses/{courseId}/progress
```

Response:

```json
{
  "courseId": "...",
  "totalLessons": 24,
  "completedLessons": 6,
  "progressPercent": 25.0
}
```

## 6. Checkout And Mock Payments

### Start Checkout

```http
POST /api/courses/{courseId}/checkout
```

Response:

```json
{
  "orderId": "...",
  "paymentUrl": "http://localhost:3000/payment/mock?orderId=..."
}
```

Frontend should redirect user to `paymentUrl`.

### Mock Payment Success

```http
POST /api/payments/mock/{orderId}/success
```

Marks order as `PAID` and grants course access.

Response:

```json
{
  "id": "...",
  "courseId": "...",
  "courseTitle": "Matura podstawowa - kurs testowy",
  "amount": 199.0,
  "status": "PAID",
  "paidAt": "2026-07-14T12:00:00",
  "accessGranted": true
}
```

### Mock Payment Cancel / Fail

```http
POST /api/payments/mock/{orderId}/cancel
POST /api/payments/mock/{orderId}/fail
```

### My Orders

```http
GET /api/me/orders
GET /api/me/orders/{orderId}
```

Use `GET /api/me/orders/{orderId}` after returning from mock payment screen to verify `status` and `accessGranted`.

Order statuses:

```text
PENDING
PAID
CANCELLED
FAILED
```

## 7. Dashboard

### Student Dashboard

```http
GET /api/me/dashboard
```

Use this for the logged-in student dashboard.

Expected use:

```text
show purchased courses
show progress
show continue lesson/course cards
```

### My Enrolled Courses

```http
GET /api/me/courses
```

Returns courses the student has access to.

## 8. Admin

All admin endpoints require `ROLE_ADMIN`.

### Courses

```http
GET /api/admin/courses
POST /api/admin/courses
PUT /api/admin/courses/{courseId}
DELETE /api/admin/courses/{courseId}
PATCH /api/admin/courses/{courseId}/publish
PATCH /api/admin/courses/{courseId}/unpublish
```

Create/update course request:

```json
{
  "title": "Matura podstawowa",
  "description": "...",
  "category": "Matura",
  "thumbnailUrl": "https://...",
  "price": 199.0
}
```

### Chapters

```http
GET /api/courses/{courseId}/chapters
POST /api/admin/courses/{courseId}/chapters
PUT /api/admin/chapters/{chapterId}
DELETE /api/admin/chapters/{chapterId}
```

Create/update chapter request:

```json
{
  "title": "Funkcje",
  "position": 0
}
```

### Lessons

```http
GET /api/chapters/{chapterId}/lessons
POST /api/admin/chapters/{chapterId}/lessons
PUT /api/admin/lessons/{lessonId}
DELETE /api/admin/lessons/{lessonId}
```

Create/update lesson request:

```json
{
  "title": "Funkcja liniowa",
  "description": "...",
  "position": 1,
  "videoUrl": "https://...",
  "pdfUrl": "https://...",
  "free": false,
  "durationMinutes": 32
}
```

`videoUrl` and `pdfUrl` remain in lesson create/update for backward compatibility.
New admin frontend should use the dedicated lesson media endpoints below.

### Lesson Materials

```http
POST /api/admin/lessons/{lessonId}/materials
PUT /api/admin/lesson-materials/{materialId}
DELETE /api/admin/lesson-materials/{materialId}
```

### Lesson Video

Use this endpoint for the main video assigned to a lesson. It creates the `VIDEO` material if it does not exist, otherwise it updates the existing one.

```http
PUT /api/admin/lessons/{lessonId}/video
```

Request for an external MP4/HLS URL:

```json
{
  "provider": "EXTERNAL_URL",
  "url": "https://example.com/video.mp4",
  "providerAssetId": null,
  "durationSeconds": 1920,
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "status": "READY"
}
```

Future Cloudflare Stream request:

```json
{
  "provider": "CLOUDFLARE_STREAM",
  "url": null,
  "providerAssetId": "cloudflare-video-id",
  "durationSeconds": 1920,
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "status": "PROCESSING"
}
```

Response is a `LessonMaterialResponse` with `type = VIDEO` and `position = 0`.

### Lesson PDF

Use this endpoint for the main PDF assigned to a lesson. It creates the `PDF` material if it does not exist, otherwise it updates the existing one.

```http
PUT /api/admin/lessons/{lessonId}/pdf
```

Request:

```json
{
  "title": "Notatka PDF",
  "url": "https://example.com/notatka.pdf",
  "position": 1
}
```

Response is a `LessonMaterialResponse` with `type = PDF`, `provider = EXTERNAL_URL`, and `status = READY`.

Create/update material request:

```json
{
  "title": "Wideo lekcji",
  "type": "VIDEO",
  "url": "https://example.com/video.mp4",
  "provider": "EXTERNAL_URL",
  "providerAssetId": null,
  "durationSeconds": 1920,
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "status": "READY",
  "position": 0
}
```

Material types:

```text
VIDEO
PDF
LINK
ATTACHMENT
```

Material providers:

```text
EXTERNAL_URL
CLOUDFLARE_STREAM
```

Validation:

```text
EXTERNAL_URL requires url.
CLOUDFLARE_STREAM requires providerAssetId.
```

Material statuses:

```text
READY
PROCESSING
FAILED
```

### Users And Access

```http
GET /api/admin/users
GET /api/admin/users/{userId}
POST /api/admin/users/{userId}/courses/{courseId}/grant-access
```

### Orders

```http
GET /api/admin/orders
PATCH /api/admin/orders/{orderId}/mark-paid
PATCH /api/admin/orders/{orderId}/cancel
PATCH /api/admin/orders/{orderId}/fail
```

## 9. Recommended Frontend Flow

### App Boot

```text
1. Read JWT from storage.
2. If token exists, call GET /api/me.
3. Store user and role in auth state.
4. If /api/me fails, clear token.
```

### Course Catalog

```text
1. GET /api/courses/catalog.
2. Render cards.
3. If hasAccess or canStart, show start/continue CTA.
4. Otherwise show buy CTA.
```

### Course Detail

```text
1. GET /api/courses/{courseId}.
2. GET /api/courses/{courseId}/access.
3. GET /api/courses/{courseId}/content.
4. Render curriculum, locked lessons, completed lessons and CTA.
```

### Buy Course

```text
1. POST /api/courses/{courseId}/checkout.
2. Redirect to response.paymentUrl.
3. Mock screen calls POST /api/payments/mock/{orderId}/success.
4. Mock screen calls GET /api/me/orders/{orderId}.
5. If status=PAID and accessGranted=true, redirect to course start/player.
```

### Start Player

```text
1. GET /api/courses/{courseId}/start.
2. Navigate to /lessons/{lessonId} in frontend.
3. GET /api/lessons/{lessonId}/play.
4. Render video, PDF/materials, previous/next buttons.
5. POST /api/lessons/{lessonId}/complete when user completes lesson.
```

## 10. Common Error Handling

Expected response body for most domain errors:

```json
{
  "message": "Error message"
}
```

Common statuses:

```text
400 validation error
401 invalid credentials or missing/invalid token
403 no access
404 resource not found
409 conflict, for example duplicate order or invalid order status
```
