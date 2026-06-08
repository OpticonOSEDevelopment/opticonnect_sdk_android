# Repository Instructions

- After making any code, test, documentation, or configuration change in this WSL checkout, sync the changed file(s) back to the Windows working copy at `/mnt/c/Users/RBO/Desktop/projects/opticonnect_sdk_android`.
- Preserve relative paths when syncing. Prefer `rsync -av --relative <changed paths> /mnt/c/Users/RBO/Desktop/projects/opticonnect_sdk_android/` for multiple files, or an exact destination path for a single file.
- Verify synced files with `cmp -s` when practical.
- Only sync files changed for the current task; do not copy unrelated dirty worktree changes unless the user explicitly asks for a full sync.
