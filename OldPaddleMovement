+        @Override
+        public boolean onTouchEvent(MotionEvent motionEvent) {
+
+            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
+
+                // Player has touched the screen
+                case MotionEvent.ACTION_DOWN:
+
+                    paused = false;
+                    if (motionEvent.getX() < screenX / 2 && motionEvent.getY() > screenY / 2) {
+                        paddle1.setMovementState(paddle1.LEFT);
+                    }
+
+                    if (motionEvent.getX() > screenX / 2 && motionEvent.getY() > screenY / 2) {
+                        paddle1.setMovementState(paddle1.RIGHT);
+                    }
+                    if (motionEvent.getX() > screenX / 2 && motionEvent.getY() < screenY / 2) {
+                        paddle2.setMovementState(paddle2.RIGHT);
+                    }
+                    if (motionEvent.getX() < screenX / 2 && motionEvent.getY() < screenY / 2){
+                        paddle2.setMovementState((paddle2.LEFT));
+                    }
+                    break;
+
+                // Player has removed finger from screen
+                case MotionEvent.ACTION_UP:
+
+                    paddle1.setMovementState(paddle1.STOPPED);
+                    paddle2.setMovementState(paddle1.STOPPED);
+                    break;
+            }
+            return true;
+        }
