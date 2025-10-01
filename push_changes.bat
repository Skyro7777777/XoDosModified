@echo off
echo Adding all changes...
git add .

echo.
set /p commit_msg="Enter commit message: "
if "%commit_msg%"=="" set commit_msg=Update modifications

echo.
echo Committing changes...
git commit -m "%commit_msg%"

echo.
echo Pushing to XoDosModified repository...
git push modified main

echo.
echo Done! Changes pushed to https://github.com/Skyro7777777/XoDosModified
pause
